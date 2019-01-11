package course.design;

/**
 * Create by ming on 19-1-7 上午1:36
 * <p>
 * 进程控制块PCB
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class PCB {

    /**
     * 进程名
     */
    private String processName;

    /**
     * 到达时间
     */
    private int arrivalTime;

    /**
     * 需要的运行时间
     */
    private int needTime;

    /**
     * 剩余时间
     */
    private int remainingTime;

    /**
     * 周转时间
     */
    private int turnAroundTime;

    /**
     * 进程状态
     */
    private ProcessState processState;

    private int jobId;

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getNeedTime() {
        return needTime;
    }

    public void setNeedTime(int needTime) {
        this.needTime = needTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public void setTurnAroundTime(int turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    @Override
    public String toString() {
        return "PCB{" +
                "processName='" + processName + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", needTime=" + needTime +
                ", remainingTime=" + remainingTime +
                ", turnAroundTime=" + turnAroundTime +
                ", processState=" + processState +
                '}';
    }

    public enum ProcessState {
        /**
         * 就绪
         */
        WAIT,
        /**
         * 运行
         */
        RUN,
        /**
         * 完成
         */
        FINISH
    }
}
