package arithmetic;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;

/**
 * 程序入口类。
 *
 * <p>在事件分发线程中创建并显示主窗口，使用 Windows 系统外观，
 * 使控件风格、标题栏和高 DPI 缩放与操作系统保持一致；同时统一
 * 设置中文字体，保证对话框和控件中的中文清晰可读。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public final class ArithmeticExerciseApp {

    /** 工具类不允许实例化。 */
    private ArithmeticExerciseApp() {
    }

    /**
     * 程序入口。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // 如果系统外观加载失败，则使用默认外观，不影响程序运行
                System.err.println("无法加载系统外观，使用默认外观：" + ex.getMessage());
            }
            Font uiFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
            Font titleFont = new Font("Microsoft YaHei", Font.BOLD, 13);
            UIManager.put("OptionPane.messageFont", uiFont);
            UIManager.put("OptionPane.buttonFont", uiFont);
            UIManager.put("OptionPane.okButtonText", "确定");
            UIManager.put("OptionPane.cancelButtonText", "取消");
            UIManager.put("OptionPane.yesButtonText", "是");
            UIManager.put("OptionPane.noButtonText", "否");
            UIManager.put("Label.font", uiFont);
            UIManager.put("Button.font", uiFont);
            UIManager.put("TextField.font", uiFont);
            UIManager.put("ComboBox.font", uiFont);
            UIManager.put("Table.font", uiFont);
            UIManager.put("TitledBorder.font", titleFont);
            new MainFrame().setVisible(true);
        });
    }
}
