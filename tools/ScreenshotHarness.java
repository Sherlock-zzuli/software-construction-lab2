package arithmetic;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;

/**
 * 截图工具（仅用于实验报告，不参与主程序编译）。
 *
 * <p>启动真实的 {@link MainFrame}，在事件分发线程上通过 {@code doClick()}
 * 触发真实按钮事件，等待界面渲染后用 {@link Robot#createScreenCapture}
 * 截取屏幕上的真实窗口。对于模态对话框，先截取主窗口与对话框的并集，
 * 再按 Enter 关闭对话框。截图包含 Windows 标题栏，不使用任何离屏绘制。</p>
 *
 * <p>用法：{@code java arithmetic.ScreenshotHarness <mode> <outputDir>}，
 * mode 取 main、generated、invalid、export。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public final class ScreenshotHarness {

    private static MainFrame frame;
    private static Robot robot;
    private static JComboBox<?> typeBox;
    private static JTextField countField;
    private static JTextField columnField;
    private static JTextField pathField;
    private static JButton generateButton;
    private static JButton exportButton;
    private static File outputDir;

    private ScreenshotHarness() {
    }

    /**
     * 程序入口。
     *
     * @param args 第一个参数为模式，第二个参数为输出目录
     * @throws Exception 当初始化或截图失败时抛出
     */
    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "main";
        outputDir = new File(args.length > 1 ? args[1] : "screenshots");
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IllegalStateException("无法创建截图目录：" + outputDir.getAbsolutePath());
        }
        robot = new Robot();
        robot.setAutoDelay(60);

        SwingUtilities.invokeAndWait(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("系统外观不可用：" + ex.getMessage());
            }
            Font uiFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
            UIManager.put("OptionPane.messageFont", uiFont);
            UIManager.put("OptionPane.buttonFont", uiFont);
            UIManager.put("OptionPane.okButtonText", "确定");
            UIManager.put("OptionPane.cancelButtonText", "取消");
            frame = new MainFrame();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            try {
                typeBox = (JComboBox<?>) getField("typeBox");
                countField = (JTextField) getField("countField");
                columnField = (JTextField) getField("columnField");
                pathField = (JTextField) getField("pathField");
                generateButton = (JButton) getField("generateButton");
                exportButton = (JButton) getField("exportButton");
            } catch (ReflectiveOperationException ex) {
                throw new IllegalStateException(ex);
            }
        });
        Thread.sleep(1200);

        switch (mode) {
            case "main":
                captureFrame("04-main.png");
                break;
            case "full":
                captureScreen("_debug-robot-full.png");
                break;
            case "hold-main":
                System.out.println("READY");
                Thread.sleep(120000);
                return;
            case "hold-generated":
                generate(30, 5);
                System.out.println("READY");
                Thread.sleep(120000);
                return;
            case "hold-invalid":
                holdInvalidInput();
                return;
            case "hold-export":
                holdExportSuccess();
                return;
            case "generated":
                generate(30, 5);
                captureFrame("05-generated.png");
                break;
            case "invalid":
                captureInvalidInput();
                break;
            case "export":
                captureExportSuccess();
                break;
            default:
                throw new IllegalArgumentException("未知截图模式：" + mode);
        }

        SwingUtilities.invokeAndWait(() -> frame.dispose());
        System.out.println("截图完成：" + outputDir.getAbsolutePath());
        System.exit(0);
    }

    /**
     * 生成指定题量和列数的四则混合题。
     *
     * @param count   题量
     * @param columns 列数
     * @throws Exception 当 EDT 调用失败时抛出
     */
    private static void generate(int count, int columns) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            typeBox.setSelectedItem(QuestionType.THREE_FOUR_MIXED);
            countField.setText(String.valueOf(count));
            columnField.setText(String.valueOf(columns));
            generateButton.doClick();
        });
        Thread.sleep(900);
    }

    /**
     * 保持题量非法对话框打开，供外部 Win32 截图工具捕获。
     *
     * @throws Exception 当 EDT 调用失败时抛出
     */
    private static void holdInvalidInput() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            typeBox.setSelectedItem(QuestionType.THREE_FOUR_MIXED);
            countField.setText("abc");
            columnField.setText("5");
        });
        SwingUtilities.invokeLater(generateButton::doClick);
        System.out.println("READY");
        Thread.sleep(120000);
    }

    /**
     * 保持导出成功对话框打开，供外部 Win32 截图工具捕获。
     *
     * @throws Exception 当 EDT 调用失败时抛出
     */
    private static void holdExportSuccess() throws Exception {
        generate(30, 5);
        File csvFile = new File("output/四则运算练习_30题_5列.csv").getAbsoluteFile();
        SwingUtilities.invokeAndWait(() -> pathField.setText(csvFile.getAbsolutePath()));
        SwingUtilities.invokeLater(exportButton::doClick);
        System.out.println("READY");
        Thread.sleep(120000);
    }

    /**
     * 截取题量非法时的提示对话框。
     *
     * @throws Exception 当截图失败时抛出
     */
    private static void captureInvalidInput() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            typeBox.setSelectedItem(QuestionType.THREE_FOUR_MIXED);
            countField.setText("abc");
            columnField.setText("5");
        });
        Thread capturer = new Thread(() -> {
            try {
                Thread.sleep(1000);
                bringToFront();
                captureUnion("06-invalid.png");
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        capturer.start();
        SwingUtilities.invokeLater(generateButton::doClick);
        capturer.join(10000);
        Thread.sleep(600);
    }

    /**
     * 截取 CSV 导出成功对话框。
     *
     * @throws Exception 当截图失败时抛出
     */
    private static void captureExportSuccess() throws Exception {
        generate(30, 5);
        File csvFile = new File("output/四则运算练习_30题_5列.csv").getAbsoluteFile();
        SwingUtilities.invokeAndWait(() -> pathField.setText(csvFile.getAbsolutePath()));
        Thread capturer = new Thread(() -> {
            try {
                Thread.sleep(1200);
                bringToFront();
                captureUnion("07-export.png");
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        capturer.start();
        SwingUtilities.invokeLater(exportButton::doClick);
        capturer.join(12000);
        Thread.sleep(600);
    }

    /**
     * 把窗口和当前可见的模态对话框置于最前。
     *
     * @throws Exception 当 EDT 调用失败时抛出
     */
    private static void bringToFront() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            frame.toFront();
            frame.setAlwaysOnTop(true);
            for (Window window : Window.getWindows()) {
                if (window.isVisible() && window != frame) {
                    window.setAlwaysOnTop(true);
                    window.toFront();
                }
            }
        });
        Thread.sleep(900);
    }

    /**
     * 截取主窗口区域。
     *
     * @param fileName 输出文件名
     * @throws Exception 当截图或写文件失败时抛出
     */
    private static void captureFrame(String fileName) throws Exception {
        Rectangle before = frame.getBounds();
        bringToFront();
        Rectangle bounds = frame.getBounds();
        System.out.println("bounds before=" + before + " after=" + bounds
                + " insets=" + frame.getInsets()
                + " root=" + frame.getRootPane().getLocationOnScreen());
        BufferedImage image = robot.createScreenCapture(clampToScreen(bounds));
        save(image, fileName);
        SwingUtilities.invokeAndWait(() -> frame.setAlwaysOnTop(false));
    }

    /**
     * 截取整个屏幕（仅用于诊断 DPI/标题栏问题）。
     *
     * @param fileName 输出文件名
     * @throws Exception 当截图或写文件失败时抛出
     */
    private static void captureScreen(String fileName) throws Exception {
        bringToFront();
        Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage image = robot.createScreenCapture(screen);
        save(image, fileName);
        SwingUtilities.invokeAndWait(() -> frame.setAlwaysOnTop(false));
    }

    /**
     * 截取主窗口与可见模态对话框的并集。
     *
     * @param fileName 输出文件名
     * @throws Exception 当截图或写文件失败时抛出
     */
    private static void captureUnion(String fileName) throws Exception {
        Rectangle bounds = frame.getBounds();
        for (Window window : Window.getWindows()) {
            if (window.isVisible() && window != frame && window.getOwner() == frame) {
                bounds = bounds.union(window.getBounds());
            }
        }
        BufferedImage image = robot.createScreenCapture(clampToScreen(bounds));
        save(image, fileName);
    }

    /**
     * 把截图区域限制在屏幕范围内。
     *
     * @param bounds 原始区域
     * @return 屏幕内的区域
     */
    private static Rectangle clampToScreen(Rectangle bounds) {
        Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        Rectangle intersection = bounds.intersection(screen);
        if (intersection.isEmpty()) {
            throw new IllegalStateException("截图区域不在屏幕内：" + bounds);
        }
        return intersection;
    }

    /**
     * 保存 PNG 并打印尺寸。
     *
     * @param image    截图
     * @param fileName 文件名
     * @throws Exception 当写文件失败时抛出
     */
    private static void save(BufferedImage image, String fileName) throws Exception {
        File file = new File(outputDir, fileName);
        ImageIO.write(image, "png", file);
        System.out.println("已保存：" + file.getAbsolutePath()
                + "（" + image.getWidth() + "×" + image.getHeight() + "）");
    }

    /**
     * 通过反射读取 MainFrame 的私有控件。
     *
     * @param name 字段名
     * @return 字段值
     * @throws ReflectiveOperationException 当字段不存在或不可访问时抛出
     */
    private static Object getField(String name) throws ReflectiveOperationException {
        Field field = MainFrame.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(frame);
    }
}
