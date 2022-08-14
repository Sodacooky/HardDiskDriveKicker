import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class FileWritingThreadWorker {

    //当前实例的工作目录
    private File toWritePath;
    //线程工作信号
    private Boolean isKeepRunning;

    public FileWritingThreadWorker(File toWritePath) {
        this.toWritePath = toWritePath;
    }

    /**
     * 循环写入
     */
    public void run() {
        isKeepRunning = true;
        while (isKeepRunning) {
            //再判断可用性
            if (!toWritePath.exists() || !toWritePath.canWrite()) {
                System.out.println(toWritePath.getPath() + " 不可用");
            } else {
                //写入
                File file = new File(toWritePath, "HardDiskDriveKicker.txt");
                //初次写入？
                if (file.exists()) {
                    //已存在，更新文件
                    try {
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(new Random().nextInt());
                        fileWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    //创建文件，先不写入，等下次，因为创建文件效果
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            //等待
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 发送停止信号
     */
    public void signalStop() {
        isKeepRunning = false;
    }


}
