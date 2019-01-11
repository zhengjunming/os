package course.design;

/**
 * Create by ming on 19-1-7 上午1:37
 * <p>
 * 作业控制块JCB
 *
 * @author ming
 * I'm the one to ignite the darkened skies.
 */
public class JCB {
    /**
     * 作业ID
     */
    private int jobId;

    /**
     * 作业大小
     */
    private int size;

    /**
     * 作业名称
     */
    private String jobName;

    /**
     * 到达时间
     */
    private int arrivalTime;

    /**
     * 需要时间
     */
    private int needTime;

    /**
     * 剩余时间
     */
    private int remainingTime;
    private int finishTime;
    /**
     * 作业状态
     */
    private JobState jobState;

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
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

    public JobState getJobState() {
        return jobState;
    }

    public void setJobState(JobState jobState) {
        this.jobState = jobState;
    }

    @Override
    public String toString() {
        return "JCB{" +
                "jobId=" + jobId +
                ", size=" + size +
                ", jobName='" + jobName + '\'' +
                ", arrivalTime=" + arrivalTime +
                ", needTime=" + needTime +
                ", jobState=" + jobState +
                '}';
    }

    public enum JobState {
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
