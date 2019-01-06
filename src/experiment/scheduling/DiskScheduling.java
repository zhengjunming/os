package experiment.scheduling;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Create by ming on 19-1-1 下午3:59
 * <p>
 * 磁盘调度
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class DiskScheduling {

    private DecimalFormat format = new DecimalFormat("#.0");

    public static void main(String[] args) {
        List<Integer> fcfsTracks = new ArrayList<>();
        List<Integer> sstfTracks = new ArrayList<>();
        List<Integer> scanTracks = new ArrayList<>();
        List<Integer> cscanTracks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int num = (int) (1 + Math.random() * 250);
            fcfsTracks.add(num);
            sstfTracks.add(num);
            scanTracks.add(num);
            cscanTracks.add(num);
        }
        DiskScheduling diskScheduling = new DiskScheduling();
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("磁盘调度算法：");
        System.out.println("随机生成的十个数为：" + fcfsTracks);
        System.out.println("\n先来先服务算法：");
        diskScheduling.fcfs(100, fcfsTracks);
        System.out.println("\n最短寻道时间优先算法：");
        diskScheduling.sstf(100, sstfTracks);
        System.out.println("\n扫描(SCAN)算法：");
        diskScheduling.scan(100, scanTracks);
        System.out.println("\n循环扫描(CSCAN)算法：");
        diskScheduling.cscan(100, cscanTracks);
        System.out.println("------------------------------------------------------------------------------------");

    }

    public void fcfs(int currentTrack, List<Integer> tracks) {
        System.out.print("磁头移动顺序：");
        double sum = 0.0;
        StringBuilder result = new StringBuilder();
        for (Integer track : tracks) {
            sum += Math.abs(currentTrack - track);
            currentTrack = track;
            result.append(track).append(" --> ");
        }
        result.replace(result.lastIndexOf(" --> "), result.length(), "");
        System.out.println(result.toString());
        double avgSeekLength = sum / tracks.size();
        System.out.println("平均移动磁道数：" + format.format(avgSeekLength));
    }

    public void sstf(int currentTrack, List<Integer> tracks) {
        System.out.print("磁头移动顺序：");
        double sum = 0.0;
        int divide = tracks.size();
        StringBuilder stringBuilder = new StringBuilder();
        int i = tracks.size();
        while (i > 0) {
            int finalCurrentTrack = currentTrack;
            tracks.sort(Comparator.comparingInt(track -> Math.abs(finalCurrentTrack - track)));
            stringBuilder.append(tracks.get(0)).append(" --> ");
            sum += Math.abs(currentTrack - tracks.get(0));
            currentTrack = tracks.get(0);
            tracks.remove(0);
            i--;
        }
        stringBuilder.replace(stringBuilder.lastIndexOf(" --> "), stringBuilder.length(), "");
        System.out.println(stringBuilder.toString());
        double avgSeekLength = sum / divide;
        System.out.println("平均移动磁道数：" + format.format(avgSeekLength));

    }

    public void scan(int currentTrack, List<Integer> tracks) {
        System.out.print("磁头移动顺序：");
        double sum = 0.0;
        tracks.add(currentTrack);
        StringBuilder result = new StringBuilder();
        tracks.sort(Comparator.comparing(track -> track));
        int index = tracks.indexOf(currentTrack);
        for (int i = index + 1; i < tracks.size(); i++) {
            sum += Math.abs(currentTrack - tracks.get(i));
            currentTrack = tracks.get(i);
            result.append(tracks.get(i)).append(" --> ");
        }
        for (int i = index - 1; i >= 0; i--) {
            sum += Math.abs(currentTrack - tracks.get(i));
            currentTrack = tracks.get(i);
            result.append(tracks.get(i)).append(" --> ");
        }
        result.replace(result.lastIndexOf(" --> "), result.length(), "");
        double avgSeekLength = sum / (tracks.size() - 1);
        System.out.println(result.toString());
        System.out.println("平均移动磁道数：" + format.format(avgSeekLength));
    }

    public void cscan(int currentTrack, List<Integer> tracks) {
        System.out.print("磁头移动顺序：");
        double sum = 0.0;
        StringBuilder result = new StringBuilder();
        tracks.add(currentTrack);
        tracks.sort(Comparator.comparing(track -> track));
        int index = tracks.indexOf(currentTrack);
        for (int i = index + 1; i < tracks.size(); i++) {
            result.append(tracks.get(i)).append(" --> ");
            sum += Math.abs(currentTrack - tracks.get(i));
            currentTrack = tracks.get(i);
        }
        for (int i = 0; i < index; i++) {
            result.append(tracks.get(i)).append(" --> ");
            sum += Math.abs(currentTrack - tracks.get(i));
            currentTrack = tracks.get(i);
        }
        result.replace(result.lastIndexOf(" --> "), result.length(), "");
        System.out.println(result.toString());
        double avgSeekLength = sum / (tracks.size() - 1);
        System.out.println("平均移动磁道数：" + format.format(avgSeekLength));
    }
}
