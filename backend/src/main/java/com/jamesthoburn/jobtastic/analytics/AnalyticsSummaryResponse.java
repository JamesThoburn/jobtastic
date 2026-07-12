package com.jamesthoburn.jobtastic.analytics;

import java.util.List;

public class AnalyticsSummaryResponse {
    private long totalApplications;
    private long activeApplications;
    private double responseRate;
    private double offerRate;
    private List<StatusBreakdownEntry> statusBreakdown;

    public AnalyticsSummaryResponse(Long totalApplications, Long activeApplications, Float responseRate, Float offerRate, List<StatusBreakdownEntry> statusBreakdown) {
        this.totalApplications = totalApplications;
        this.activeApplications = activeApplications;
        this.responseRate = responseRate;
        this.offerRate = offerRate;
        this.statusBreakdown = statusBreakdown;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getActiveApplications() {
        return activeApplications;
    }

    public void setActiveApplications(long activeApplications) {
        this.activeApplications = activeApplications;
    }

    public double getResponseRate() {
        return responseRate;
    }

    public void setResponseRate(double responseRate) {
        this.responseRate = responseRate;
    }

    public double getOfferRate() {
        return offerRate;
    }

    public void setOfferRate(double offerRate) {
        this.offerRate = offerRate;
    }

    public List<StatusBreakdownEntry> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(List<StatusBreakdownEntry> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }
}

