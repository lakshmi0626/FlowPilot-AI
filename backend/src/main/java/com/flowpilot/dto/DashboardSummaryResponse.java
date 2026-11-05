package com.flowpilot.dto;

import java.util.List;

public class DashboardSummaryResponse {

    private long totalEmployees;
    private long activeWorkflows;
    private long totalWorkflows;
    private long pendingTasks;
    private long completedTasks;
    private long overdueTasks;
    private long pendingApprovals;
    private double workflowCompletionRate;
    private List<ChartDataPoint> taskTrends;
    private List<ChartDataPoint> departmentPerformance;
    private List<ChartDataPoint> workflowStatusDistribution;

    public DashboardSummaryResponse() {}

    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long totalEmployees) { this.totalEmployees = totalEmployees; }

    public long getActiveWorkflows() { return activeWorkflows; }
    public void setActiveWorkflows(long activeWorkflows) { this.activeWorkflows = activeWorkflows; }

    public long getTotalWorkflows() { return totalWorkflows; }
    public void setTotalWorkflows(long totalWorkflows) { this.totalWorkflows = totalWorkflows; }

    public long getPendingTasks() { return pendingTasks; }
    public void setPendingTasks(long pendingTasks) { this.pendingTasks = pendingTasks; }

    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }

    public long getOverdueTasks() { return overdueTasks; }
    public void setOverdueTasks(long overdueTasks) { this.overdueTasks = overdueTasks; }

    public long getPendingApprovals() { return pendingApprovals; }
    public void setPendingApprovals(long pendingApprovals) { this.pendingApprovals = pendingApprovals; }

    public double getWorkflowCompletionRate() { return workflowCompletionRate; }
    public void setWorkflowCompletionRate(double workflowCompletionRate) { this.workflowCompletionRate = workflowCompletionRate; }

    public List<ChartDataPoint> getTaskTrends() { return taskTrends; }
    public void setTaskTrends(List<ChartDataPoint> taskTrends) { this.taskTrends = taskTrends; }

    public List<ChartDataPoint> getDepartmentPerformance() { return departmentPerformance; }
    public void setDepartmentPerformance(List<ChartDataPoint> departmentPerformance) { this.departmentPerformance = departmentPerformance; }

    public List<ChartDataPoint> getWorkflowStatusDistribution() { return workflowStatusDistribution; }
    public void setWorkflowStatusDistribution(List<ChartDataPoint> workflowStatusDistribution) { this.workflowStatusDistribution = workflowStatusDistribution; }
}
