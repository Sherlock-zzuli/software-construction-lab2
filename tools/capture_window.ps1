param(
    [Parameter(Mandatory = $true)][string]$TitleLike,
    [string]$OutputPath = "",
    [string]$ProcessName = "",
    [int]$Width = 0,
    [int]$Height = 0,
    [switch]$Maximize,
    [switch]$FocusOnly
)

$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing
Add-Type -TypeDefinition @'
using System;
using System.Text;
using System.Diagnostics;
using System.Runtime.InteropServices;

public static class WindowCaptureNative
{
    public delegate bool EnumWindowsProc(IntPtr hWnd, IntPtr lParam);

    [DllImport("user32.dll")]
    private static extern bool EnumWindows(EnumWindowsProc lpEnumFunc, IntPtr lParam);

    [DllImport("user32.dll", CharSet = CharSet.Unicode)]
    private static extern int GetWindowText(IntPtr hWnd, StringBuilder lpString, int nMaxCount);

    [DllImport("user32.dll")]
    private static extern int GetWindowTextLength(IntPtr hWnd);

    [DllImport("user32.dll")]
    private static extern bool IsWindowVisible(IntPtr hWnd);

    [DllImport("user32.dll")]
    public static extern bool GetWindowRect(IntPtr hWnd, out RECT lpRect);

    [DllImport("user32.dll")]
    public static extern bool SetForegroundWindow(IntPtr hWnd);

    [DllImport("user32.dll")]
    public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);

    [DllImport("user32.dll")]
    public static extern bool BringWindowToTop(IntPtr hWnd);

    [DllImport("user32.dll")]
    public static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint lpdwProcessId);

    [DllImport("user32.dll")]
    public static extern bool SetProcessDPIAware();

    [DllImport("kernel32.dll")]
    private static extern uint GetCurrentThreadId();

    [DllImport("user32.dll")]
    private static extern bool AttachThreadInput(uint idAttach, uint idAttachTo, bool fAttach);

    [DllImport("user32.dll")]
    private static extern IntPtr SetFocus(IntPtr hWnd);

    [DllImport("user32.dll")]
    public static extern bool SetWindowPos(IntPtr hWnd, IntPtr hWndInsertAfter,
        int X, int Y, int cx, int cy, uint uFlags);

    [StructLayout(LayoutKind.Sequential)]
    public struct RECT
    {
        public int Left;
        public int Top;
        public int Right;
        public int Bottom;
    }

    public static IntPtr FindWindow(string titleSubstring, string processName)
    {
        IntPtr best = IntPtr.Zero;
        int bestArea = 0;
        EnumWindows(delegate(IntPtr hWnd, IntPtr lParam)
        {
            if (!IsWindowVisible(hWnd))
            {
                return true;
            }
            int length = GetWindowTextLength(hWnd);
            if (length <= 0)
            {
                return true;
            }
            StringBuilder builder = new StringBuilder(length + 1);
            GetWindowText(hWnd, builder, builder.Capacity);
            string title = builder.ToString();
            if (title.IndexOf(titleSubstring, StringComparison.OrdinalIgnoreCase) < 0)
            {
                return true;
            }
            if (!string.IsNullOrEmpty(processName))
            {
                uint processId;
                GetWindowThreadProcessId(hWnd, out processId);
                try
                {
                    Process process = Process.GetProcessById((int)processId);
                    if (process.ProcessName.IndexOf(processName,
                            StringComparison.OrdinalIgnoreCase) < 0)
                    {
                        return true;
                    }
                }
                catch (Exception)
                {
                    return true;
                }
            }
            RECT rect;
            GetWindowRect(hWnd, out rect);
            int area = (rect.Right - rect.Left) * (rect.Bottom - rect.Top);
            if (area > bestArea)
            {
                bestArea = area;
                best = hWnd;
            }
            return true;
        }, IntPtr.Zero);
        return best;
    }

    public static string GetTitle(IntPtr hWnd)
    {
        int length = GetWindowTextLength(hWnd);
        StringBuilder builder = new StringBuilder(length + 1);
        GetWindowText(hWnd, builder, builder.Capacity);
        return builder.ToString();
    }

    public static void ForceFocus(IntPtr hWnd)
    {
        uint processId;
        uint targetThread = GetWindowThreadProcessId(hWnd, out processId);
        uint currentThread = GetCurrentThreadId();
        AttachThreadInput(currentThread, targetThread, true);
        ShowWindow(hWnd, 9);
        BringWindowToTop(hWnd);
        SetForegroundWindow(hWnd);
        SetFocus(hWnd);
        AttachThreadInput(currentThread, targetThread, false);
    }
}
'@

[WindowCaptureNative]::SetProcessDPIAware() | Out-Null
$hWnd = [WindowCaptureNative]::FindWindow($TitleLike, $ProcessName)
if ($hWnd -eq [IntPtr]::Zero) {
    throw "未找到标题包含 [$TitleLike] 的窗口。"
}
$title = [WindowCaptureNative]::GetTitle($hWnd)
if ($FocusOnly) {
    [WindowCaptureNative]::ForceFocus($hWnd)
    Start-Sleep -Milliseconds 300
    Write-Output ("已聚焦窗口：{0}" -f $title)
    return
}
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    throw "OutputPath 不能为空。"
}
if ($Maximize) {
    [WindowCaptureNative]::ShowWindow($hWnd, 3) | Out-Null
} else {
    [WindowCaptureNative]::ShowWindow($hWnd, 9) | Out-Null
}
if ($Width -gt 0 -and $Height -gt 0) {
    [WindowCaptureNative]::SetWindowPos($hWnd, [IntPtr]::Zero, 0, 0, $Width, $Height, 0x0004) | Out-Null
}
[WindowCaptureNative]::SetForegroundWindow($hWnd) | Out-Null
[WindowCaptureNative]::BringWindowToTop($hWnd) | Out-Null
[WindowCaptureNative]::SetWindowPos($hWnd, [IntPtr](-1), 0, 0, 0, 0, 0x0003) | Out-Null
Start-Sleep -Milliseconds 900

$rect = New-Object WindowCaptureNative+RECT
[WindowCaptureNative]::GetWindowRect($hWnd, [ref]$rect) | Out-Null
$width = $rect.Right - $rect.Left
$height = $rect.Bottom - $rect.Top
if ($width -le 0 -or $height -le 0) {
    throw "窗口尺寸无效：${width}x${height}"
}

$outputFull = [System.IO.Path]::GetFullPath($OutputPath)
$directory = [System.IO.Path]::GetDirectoryName($outputFull)
if (-not (Test-Path $directory)) {
    New-Item -ItemType Directory -Force -Path $directory | Out-Null
}
$bitmap = New-Object System.Drawing.Bitmap($width, $height)
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.CopyFromScreen($rect.Left, $rect.Top, 0, 0, $bitmap.Size)
$graphics.Dispose()
$bitmap.Save($outputFull, [System.Drawing.Imaging.ImageFormat]::Png)
$bitmap.Dispose()
[WindowCaptureNative]::SetWindowPos($hWnd, [IntPtr](-2), 0, 0, 0, 0, 0x0003) | Out-Null

Write-Output ("已保存：{0}（{1}×{2}，窗口标题：{3}）" -f $outputFull, $width, $height, $title)
