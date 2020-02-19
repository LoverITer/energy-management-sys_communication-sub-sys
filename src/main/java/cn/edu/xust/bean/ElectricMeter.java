package cn.edu.xust.bean;

import java.math.BigDecimal;


/**
 * 电表实体类
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/18 18:28
 */
public class ElectricMeter {
    /**
     * 电表编号
     */
    private Integer electricMeterId;
    /**
     * 用户编号
     */
    private Integer userId;
    /**
     * 余额
     */
    private BigDecimal userBalance;
    /**
     * 电价
     */
    private BigDecimal electricityPrice;
    /**
     * 当前总用电量
     */
    private Double currentTotalElectricity;
    /**
     * 当前超峰时总用电量
     */
    private Double currentTotalSuperPeakElectricity;
    /**
     * 当前峰时总用电量
     */
    private Double currentTotalPeakElectricity;
    /**
     * 当前正常时间总用电量
     */
    private Double currentTotalNormalElectricity;
    /**
     * 当前谷时总用电量
     */
    private Double currentTotalValleyElectricity;


    public Integer getElectricMeterId() {
        return electricMeterId;
    }

    public void setElectricMeterId(Integer electricMeterId) {
        this.electricMeterId = electricMeterId;
    }

    public Integer getUserId() {
        return userId;
    }


    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(BigDecimal userBalance) {
        this.userBalance = userBalance;
    }

    public BigDecimal getElectricityPrice() {
        return electricityPrice;
    }

    public void setElectricityPrice(BigDecimal electricityPrice) {
        this.electricityPrice = electricityPrice;
    }

    public Double getCurrentTotalElectricity() {
        return currentTotalElectricity;
    }

    public void setCurrentTotalElectricity(Double currentTotalElectricity) {
        this.currentTotalElectricity = currentTotalElectricity;
    }

    public Double getCurrentTotalSuperPeakElectricity() {
        return currentTotalSuperPeakElectricity;
    }

    public void setCurrentTotalSuperPeakElectricity(Double currentTotalSuperPeakElectricity) {
        this.currentTotalSuperPeakElectricity = currentTotalSuperPeakElectricity;
    }

    public Double getCurrentTotalPeakElectricity() {
        return currentTotalPeakElectricity;
    }

    public void setCurrentTotalPeakElectricity(Double currentTotalPeakElectricity) {
        this.currentTotalPeakElectricity = currentTotalPeakElectricity;
    }

    public Double getCurrentTotalNormalElectricity() {
        return currentTotalNormalElectricity;
    }

    public void setCurrentTotalNormalElectricity(Double currentTotalNormalElectricity) {
        this.currentTotalNormalElectricity = currentTotalNormalElectricity;
    }

    public Double getCurrentTotalValleyElectricity() {
        return currentTotalValleyElectricity;
    }

    public void setCurrentTotalValleyElectricity(Double currentTotalValleyElectricity) {
        this.currentTotalValleyElectricity = currentTotalValleyElectricity;
    }


    @Override
    public String toString() {
        return "ElectricMeter{" +
                "electricMeterId=" + electricMeterId +
                ", userId=" + userId +
                ", userBalance=" + userBalance +
                ", electricityPrice=" + electricityPrice +
                ", currentTotalElectricity=" + currentTotalElectricity +
                ", currentTotalSuperPeakElectricity=" + currentTotalSuperPeakElectricity +
                ", currentTotalPeakElectricity=" + currentTotalPeakElectricity +
                ", currentTotalNormalElectricity=" + currentTotalNormalElectricity +
                ", currentTotalValleyElectricity=" + currentTotalValleyElectricity +
                '}';
    }
}