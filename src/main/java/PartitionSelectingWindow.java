import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class PartitionSelectingWindow extends JFrame {

    //上次选择的分区，如果为NULL则为没有
    private final List<String> lastTimeSelected;
    //储存分区与Checkbox的对应关系
    private final Map<JCheckBox, File> checkboxPartitions = new HashMap<>();
    //储存实际选择的分区
    private final List<File> selectedPartitions = new ArrayList<>();


    public PartitionSelectingWindow(List<String> lastTimeSelected) {
        //是否存在上次选择
        if (lastTimeSelected == null) this.lastTimeSelected = new ArrayList<>();
        else this.lastTimeSelected = lastTimeSelected;
        //将不存在的剔除
        List<String> existName = Arrays.stream(File.listRoots()).map(File::getPath).collect(Collectors.toList());
        this.lastTimeSelected.removeIf(s -> !existName.contains(s));
        //将上次选择拷贝到实际选择
        selectedPartitions.addAll(this.lastTimeSelected.stream().map(File::new).collect(Collectors.toList()));
        buildWindow();
    }


    /**
     * 构件窗口
     */
    private void buildWindow() {
        //窗口属性
        this.setTitle("Select partitions...");
        this.setSize(240, 320);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //两大控件添加
        //列表面板
        this.add(buildPartitionListPanel(), BorderLayout.NORTH);
        //运行按钮
        JButton jButton = new JButton("Run");
        this.add(jButton, BorderLayout.SOUTH);
        //按钮事件注册
        jButton.addActionListener(this::runButtonEventHandle);

        //显示
        this.setVisible(true);
    }

    /**
     * 读取系统分区信息，生成复选框列表，并放置到ScrollPanel中；
     * 同时根据上次选择中自动勾选部分。
     *
     * @return 生成的复选框列表滚动面板
     */
    private JPanel buildPartitionListPanel() {
        //获取根目录列表
        File[] roots = File.listRoots();
        //pane
        JPanel jPanel = new JPanel();
        jPanel.setPreferredSize(new Dimension(240, 240));
        Box verticalBox = Box.createVerticalBox();
        //遍历每个根目录，生成Checkbox，放入到Pane中
        for (File oneRoot : roots) {
            //行容器
            Box horizontalBox = Box.createHorizontalBox();

            //创建Checkbox
            JCheckBox checkbox = new JCheckBox(oneRoot.getPath() + "               ");
            //不可用设置为灰色
            checkbox.setEnabled(Files.isWritable(oneRoot.toPath()));
            //是否上次选择
            if (Files.isWritable(oneRoot.toPath()) && lastTimeSelected.contains(oneRoot.getPath())) {
                checkbox.setSelected(true);
            }
            horizontalBox.add(checkbox);
            horizontalBox.add(Box.createHorizontalGlue());
            //可否写入Label提示
            if (Files.isWritable(oneRoot.toPath())) {
                horizontalBox.add(new JLabel("Writable"));
            } else {
                JLabel label = new JLabel("Unavailable");
                label.setEnabled(false);
                horizontalBox.add(label);
            }
            //存入列box
            verticalBox.add(horizontalBox);
            //存入map
            this.checkboxPartitions.put(checkbox, oneRoot);
            //注册事件
            checkbox.addItemListener(this::checkboxEventHandle);
        }
        //box填充
        verticalBox.add(Box.createHorizontalGlue());

        //设置面板
        jPanel.add(verticalBox);
        //
        return jPanel;
    }

    private void checkboxEventHandle(ItemEvent event) {
        //获取Item，也就是Checkbox
        JCheckBox cb = (JCheckBox) event.getItem();
        //获取对应File
        File partition = this.checkboxPartitions.get(cb);
        //看看是取消还是勾选
        if (cb.isSelected()) {
            //选中，增加
            this.selectedPartitions.add(partition);
        } else {
            //取消，移除，如果有
            this.selectedPartitions.remove(partition);
        }
    }

    private void runButtonEventHandle(ActionEvent event) {
        this.selectedPartitions.forEach(System.out::println);//debug info
        //判断有没有选择
        if (selectedPartitions.size() == 0) {
            JOptionPane.showMessageDialog(this, "Select any partition!");
            return;
        }
        //将当前窗口隐藏
        this.setVisible(false);
        //将当前选择写到文件
        File file = new File("./LastTimePartition.txt");
        try {
            PrintWriter printWriter = new PrintWriter(file);
            selectedPartitions.forEach(printWriter::println);
            printWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //打开运行窗口
        new RunningWindow(selectedPartitions);
    }


}
