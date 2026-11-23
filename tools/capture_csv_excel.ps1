param(
    [Parameter(Mandatory = $true)][string]$CsvPath,
    [Parameter(Mandatory = $true)][string]$OutputPath
)

$ErrorActionPreference = "Stop"
$excel = New-Object -ComObject Excel.Application
try {
    $excel.Visible = $true
    $excel.DisplayAlerts = $false
    $workbook = $excel.Workbooks.Open($CsvPath)
    $sheet = $workbook.Worksheets.Item(1)

    $excel.WindowState = -4140
    $excel.Width = 980
    $excel.Height = 680
    $excel.Left = 120
    $excel.Top = 120

    $range = $sheet.Range("A1:E6")
    $range.Font.Name = "Microsoft YaHei"
    $range.Font.Size = 18
    $range.HorizontalAlignment = -4108
    $range.VerticalAlignment = -4108
    $sheet.Rows("1:6").RowHeight = 34
    $sheet.Columns("A:E").AutoFit() | Out-Null
    $excel.ActiveWindow.Zoom = 100
    $sheet.Range("A1").Select()

    Write-Output ("UsedRange: {0} rows x {1} columns" -f `
        $sheet.UsedRange.Rows.Count, $sheet.UsedRange.Columns.Count)
    Write-Output ("A1: {0}" -f $sheet.Range("A1").Text)
    Write-Output ("D1: {0}" -f $sheet.Range("D1").Text)
    Start-Sleep -Seconds 2

    & pwsh -NoProfile -ExecutionPolicy Bypass -File `
        (Join-Path $PSScriptRoot "capture_window.ps1") `
        -TitleLike "四则运算练习_30题_5列" `
        -OutputPath $OutputPath `
        -ProcessName "EXCEL"

    $workbook.Close($false)
} finally {
    $excel.Quit()
    [System.Runtime.InteropServices.Marshal]::ReleaseComObject($excel) | Out-Null
    [GC]::Collect()
}
