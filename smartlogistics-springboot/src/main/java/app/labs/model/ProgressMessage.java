package app.labs.model;

public class ProgressMessage {
    private Long orderId;
    private int progressState;

    // ✅ 기본 생성자
    public ProgressMessage() {}

    // ✅ 생성자
    public ProgressMessage(Long orderId, int progressState) {
        this.orderId = orderId;
        this.progressState = progressState;
    }

    // ✅ Getter & Setter
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public int getProgressState() {
        return progressState;
    }

    public void setProgressState(int progressState) {
        this.progressState = progressState;
    }
}