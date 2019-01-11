package course.design.file;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;

/**
 * Create by ming on 19-1-10 下午7:02
 * <p>
 * 文件系统
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class FileSystem {

    private static Disk disk = new Disk();

    private static Directory currentDir = new Directory();

    public FileSystem() {
        operateMenu();
        init();
    }

    private void init() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String[] inputs = scanner.nextLine().split(" ");
            String command = inputs[0].trim();

            switch (command) {
                case "mkdir": {
                    String dirname;
                    if (inputs.length == 2 && !"".equals(dirname = inputs[1].trim())) {
                        mkdir(dirname);
                    }
                    System.out.println();
                    break;
                }
                case "rmdir": {
                    String dirname;
                    if (inputs.length == 2 && !"".equals(dirname = inputs[1].trim())) {
                        rmdir(dirname);
                    }
                    System.out.println();
                    break;
                }
                case "renamedir": {
                    String expect, update;
                    if (inputs.length == 3 && !"".equals(expect = inputs[1].trim()) && !"".equals(update = inputs[2].trim())) {
                        renamedir(expect, update);
                    }
                    System.out.println();
                    break;
                }
                case "ls":
                    ls();
                    System.out.println();
                    break;
                case "cd": {
                    String dirname;
                    if (inputs.length == 2 && !"".equals(dirname = inputs[1].trim())) {
                        cd(dirname);
                    }
                    System.out.println();
                    break;
                }
                case "touch": {
                    String filename;
                    if (inputs.length == 2 && !"".equals(filename = inputs[1].trim())) {
                        touch(filename);
                    }
                    System.out.println();
                    break;
                }
                case "rm": {
                    String filename;
                    if (inputs.length == 2 && !"".equals(filename = inputs[1].trim())) {
                        rm(filename);
                    }
                    System.out.println();
                    break;
                }
                case "cat": {
                    String filename;
                    if (inputs.length == 2 && !"".equals(filename = inputs[1].trim())) {
                        cat(filename);
                    }
                    System.out.println();
                    break;
                }
                case "write": {
                    String filename;
                    if (inputs.length == 2 && !"".equals(filename = inputs[1].trim())) {
                        write(filename);
                    }
                    System.out.println();
                    break;
                }
                case "mv": {
                    String expect, update;
                    if (inputs.length == 3 && !"".equals(expect = inputs[1].trim()) && !"".equals(update = inputs[2].trim())) {
                        mv(expect, update);
                    }
                    System.out.println();
                    break;
                }
                case "show":
                    disk.showDisk();
                    System.out.println();
                    break;
                case "exit":
                    System.exit(0);
                default:
                    System.out.println("Command not found!");
                    break;
            }
        }
    }

    private void mkdir(String dirname) {
        if (currentDir.getDirectories().containsKey(dirname)) {
            System.out.println("同名目录已存在");
            return;
        }
        Directory dir = new Directory();
        dir.setDirname(dirname);
        dir.setParent(currentDir);
        currentDir.getDirectories().put(dirname, dir);
        System.out.println("目录创建成功");
    }

    private void rmdir(String dirname) {
        if (!currentDir.getDirectories().containsKey(dirname)) {
            System.out.println("目录不存在");
            return;
        }
        Directory directory = currentDir.getDirectories().get(dirname);
        if (!directory.getDirectories().isEmpty() || !directory.getFiles().isEmpty()) {
            System.out.println("只能删除空目录");
            return;
        }
        currentDir.getDirectories().remove(dirname);
        System.out.println("删除目录成功");
    }

    private void renamedir(String expect, String update) {
        if (!currentDir.getDirectories().containsKey(expect)) {
            System.out.println("目录不存在");
            return;
        }
        if (currentDir.getDirectories().containsKey(update)) {
            System.out.println("同名目录已存在");
            return;
        }
        currentDir.getDirectories().get(expect).setDirname(update);
        System.out.println("重命名成功");
    }

    private void ls() {
        Directory directory = currentDir;
        Map<String, Directory> directories = directory.getDirectories();
        Map<String, File> files = directory.getFiles();
        System.out.println("此目录包含" + directories.size() + "个子目录," + files.size() + "个文件");
        if (directories.size() != 0) {
            System.out.println("目录：");
            directories.forEach((dirname, directory1) -> System.out.println(dirname + "/"));
        }
        if (files.size() != 0) {
            System.out.println("\n文件：");
            files.forEach((filename, file) -> System.out.println(filename));
        }
    }

    private void cd(String dirname) {
        if ("..".equals(dirname)) {
            Directory parent = currentDir.getParent();
            if (parent != null) {
                currentDir = parent;
            }
        } else {
            if (!currentDir.getDirectories().containsKey(dirname)) {
                return;
            }
            currentDir = currentDir.getDirectories().get(dirname);
        }
    }

    private void touch(String filename) {
        if (currentDir.getFiles().containsKey(filename)) {
            System.out.println("文件已存在");
            return;
        }
        File file = new File();
        file.setFilename(filename);
        file.setFirstDiskBlock(disk.getFreeBlock());
        currentDir.getFiles().put(filename, file);
        System.out.println("创建文件成功");
    }

    private void rm(String filename) {
        if (!currentDir.getFiles().containsKey(filename)) {
            System.out.println("文件不存在");
            return;
        }
        File file = currentDir.getFiles().get(filename);
        currentDir.getFiles().remove(filename);
        disk.free(file.getFirstDiskBlock());
        System.out.println("删除文件成功");
    }

    private void cat(String filename) {
        File file;
        if (!currentDir.getFiles().containsKey(filename)) {
            System.out.println("文件不存在");
            return;
        }
        file = currentDir.getFiles().get(filename);
        String content = disk.getBlockData(file.getFirstDiskBlock());
        if (content != null) {
            System.out.println(content + "\n");
        }
    }

    private void write(String filename) {
        if (!currentDir.getFiles().containsKey(filename)) {
            System.out.println("文件不存在");
            return;
        }
        File file = currentDir.getFiles().get(filename);
        disk.free(file.getFirstDiskBlock());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder stringBuilder = new StringBuilder();
        System.out.println("请输入内容,quit结束");
        try {
            while (true) {
                // 获取输入
                String string = br.readLine();
                if ("quit".equals(string)) {
                    break;
                }
                stringBuilder.append(string).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 磁盘块大小
        int blockSize = Disk.getDiskBlockSize();
        // 偏移指针
        int point = 0;
        int firstBlock = -1;
        int head = -1;
        // 大于一块磁盘块的大小，需要分开存放
        if (stringBuilder.length() > blockSize) {
            for (int i = 0; i < stringBuilder.length() - blockSize; i = i + blockSize) {
                String op = stringBuilder.substring(i, i + blockSize);
                // 分配磁盘块
                head = disk.malloc(head, op);
                if (i == 0) {
                    firstBlock = head;
                }
                point = i;
            }
            String rest = stringBuilder.substring(point + blockSize, stringBuilder.length());
            disk.malloc(head, rest);
        } else {
            firstBlock = disk.malloc(head, stringBuilder.toString());
        }
        file.setFirstDiskBlock(firstBlock);
        currentDir.getFiles().put(file.getFilename(), file);
        System.out.println("写文件成功");
    }

    private void mv(String expect, String update) {
        if (!currentDir.getFiles().containsKey(expect)) {
            System.out.println("文件不存在");
            return;
        }
        if (currentDir.getFiles().containsKey(update)) {
            System.out.println("存在同名文件");
        }
        currentDir.getFiles().get(expect).setFilename(update);
        System.out.println("重命名文件成功");
    }

    private void operateMenu() {
        System.out.println("*************************************************************");
        System.out.println("                   基于FAT分配表的文件系统                      ");
        System.out.println("                        具有以下功能                           ");
        System.out.println("    1. mkdir [dir]                ： 创建文件夹                ");
        System.out.println("    2. rmdir [dir]                ： 删除文件夹                ");
        System.out.println("    3. renamedir [expect] [update]： 重命名文件夹              ");
        System.out.println("    4. ls                         ： 列出当前目录              ");
        System.out.println("    5. cd [dir]                   ： 切换目录                 ");
        System.out.println("    6. touch [filename]           ： 创建文件                 ");
        System.out.println("    7. rm [filename]              ： 删除文件                 ");
        System.out.println("    8. mv [expect] [update]       ： 重命名文件               ");
        System.out.println("    9. write [filename]           ： 写文件                   ");
        System.out.println("   10. cat [filename]             ： 读文件                   ");
        System.out.println("   11. show                       ： 查看磁盘块               ");
        System.out.println("   12. exit                       ： 退出                    ");
        System.out.println("*************************************************************");
    }
}
