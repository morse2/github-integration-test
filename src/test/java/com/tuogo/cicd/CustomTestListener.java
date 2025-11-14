package com.tuogo.cicd;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomTestListener implements ITestListener {
    
    private static final String REPORT_DIR = "target/test-reports/";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Map<String, TestReport> testReports = new HashMap<>();
    private long suiteStartTime;
    
    @Override
    public void onStart(ITestContext context) {
        suiteStartTime = System.currentTimeMillis();
        System.out.println("=== 测试套件开始执行: " + context.getName() + " ===");
        System.out.println("开始时间: " + DATE_FORMAT.format(new Date(suiteStartTime)));
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getName();
        TestReport report = new TestReport(testName);
        report.setStartTime(System.currentTimeMillis());
        testReports.put(testName, report);
        
        System.out.println("\n--- 开始测试: " + testName + " ---");
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        TestReport report = testReports.get(result.getName());
        if (report != null) {
            report.setEndTime(System.currentTimeMillis());
            report.setStatus("PASS");
            report.setExecutionTime(report.getEndTime() - report.getStartTime());
        }
        
        System.out.println("✓ 测试通过: " + result.getName());
        System.out.println("执行时间: " + (report != null ? report.getExecutionTime() + "ms" : "N/A"));
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        TestReport report = testReports.get(result.getName());
        if (report != null) {
            report.setEndTime(System.currentTimeMillis());
            report.setStatus("FAIL");
            report.setExecutionTime(report.getEndTime() - report.getStartTime());
            report.setErrorMessage(result.getThrowable().getMessage());
        }
        
        System.out.println("✗ 测试失败: " + result.getName());
        System.out.println("错误信息: " + result.getThrowable().getMessage());
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        TestReport report = testReports.get(result.getName());
        if (report != null) {
            report.setEndTime(System.currentTimeMillis());
            report.setStatus("SKIP");
            report.setExecutionTime(report.getEndTime() - report.getStartTime());
        }
        
        System.out.println("↷ 测试跳过: " + result.getName());
    }
    
    @Override
    public void onFinish(ITestContext context) {
        long suiteEndTime = System.currentTimeMillis();
        long totalExecutionTime = suiteEndTime - suiteStartTime;
        
        // 生成HTML报告
        generateHtmlReport(context, totalExecutionTime);
        // 生成文本报告
        generateTextReport(context, totalExecutionTime);
        
        System.out.println("\n=== 测试套件执行完成 ===");
        System.out.println("总执行时间: " + totalExecutionTime + "ms");
        System.out.println("测试报告已生成到: " + REPORT_DIR);
    }
    
    private void generateHtmlReport(ITestContext context, long totalExecutionTime) {
        try {
            String reportFile = REPORT_DIR + "test-report.html";
            PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
            
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang='zh-CN'>");
            writer.println("<head>");
            writer.println("    <meta charset='UTF-8'>");
            writer.println("    <title>接口测试报告</title>");
            writer.println("    <style>");
            writer.println("        body { font-family: Arial, sans-serif; margin: 20px; }");
            writer.println("        .header { background: #f5f5f5; padding: 20px; border-radius: 5px; }");
            writer.println("        .test-case { margin: 10px 0; padding: 10px; border-left: 4px solid; }");
            writer.println("        .pass { border-color: #28a745; background: #f8fff9; }");
            writer.println("        .fail { border-color: #dc3545; background: #fff5f5; }");
            writer.println("        .skip { border-color: #ffc107; background: #fffef0; }");
            writer.println("        .stats { display: flex; gap: 20px; margin: 20px 0; }");
            writer.println("        .stat { padding: 10px; border-radius: 5px; color: white; }");
            writer.println("        .total { background: #007bff; }");
            writer.println("        .passed { background: #28a745; }");
            writer.println("        .failed { background: #dc3545; }");
            writer.println("        .skipped { background: #ffc107; }");
            writer.println("    </style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("    <div class='header'>");
            writer.println("        <h1>接口自动化测试报告</h1>");
            writer.println("        <p><strong>套件名称:</strong> " + context.getName() + "</p>");
            writer.println("        <p><strong>执行时间:</strong> " + DATE_FORMAT.format(new Date()) + "</p>");
            writer.println("        <p><strong>总耗时:</strong> " + totalExecutionTime + "ms</p>");
            writer.println("    </div>");
            
            // 统计信息
            long passed = testReports.values().stream().filter(r -> "PASS".equals(r.getStatus())).count();
            long failed = testReports.values().stream().filter(r -> "FAIL".equals(r.getStatus())).count();
            long skipped = testReports.values().stream().filter(r -> "SKIP".equals(r.getStatus())).count();
            
            writer.println("    <div class='stats'>");
            writer.println("        <div class='stat total'>总测试: " + testReports.size() + "</div>");
            writer.println("        <div class='stat passed'>通过: " + passed + "</div>");
            writer.println("        <div class='stat failed'>失败: " + failed + "</div>");
            writer.println("        <div class='stat skipped'>跳过: " + skipped + "</div>");
            writer.println("    </div>");
            
            // 测试用例详情
            writer.println("    <h2>测试用例详情</h2>");
            for (TestReport report : testReports.values()) {
                String statusClass = "";
                switch (report.getStatus()) {
                    case "PASS": statusClass = "pass"; break;
                    case "FAIL": statusClass = "fail"; break;
                    case "SKIP": statusClass = "skip"; break;
                }
                
                writer.println("    <div class='test-case " + statusClass + "'>");
                writer.println("        <h3>" + report.getTestName() + "</h3>");
                writer.println("        <p><strong>状态:</strong> " + report.getStatus() + "</p>");
                writer.println("        <p><strong>执行时间:</strong> " + report.getExecutionTime() + "ms</p>");
                if (report.getErrorMessage() != null) {
                    writer.println("        <p><strong>错误信息:</strong> " + report.getErrorMessage() + "</p>");
                }
                writer.println("        <p><strong>开始时间:</strong> " + DATE_FORMAT.format(new Date(report.getStartTime())) + "</p>");
                writer.println("        <p><strong>结束时间:</strong> " + DATE_FORMAT.format(new Date(report.getEndTime())) + "</p>");
                writer.println("    </div>");
            }
            
            writer.println("</body>");
            writer.println("</html>");
            writer.close();
            
        } catch (IOException e) {
            System.err.println("生成HTML报告失败: " + e.getMessage());
        }
    }
    
    private void generateTextReport(ITestContext context, long totalExecutionTime) {
        try {
            String reportFile = REPORT_DIR + "test-report.txt";
            PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
            
            writer.println("=== 接口自动化测试报告 ===");
            writer.println("套件名称: " + context.getName());
            writer.println("生成时间: " + DATE_FORMAT.format(new Date()));
            writer.println("总执行时间: " + totalExecutionTime + "ms");
            writer.println();
            
            // 统计信息
            long passed = testReports.values().stream().filter(r -> "PASS".equals(r.getStatus())).count();
            long failed = testReports.values().stream().filter(r -> "FAIL".equals(r.getStatus())).count();
            long skipped = testReports.values().stream().filter(r -> "SKIP".equals(r.getStatus())).count();
            
            writer.println("=== 统计信息 ===");
            writer.println("总测试数: " + testReports.size());
            writer.println("通过: " + passed);
            writer.println("失败: " + failed);
            writer.println("跳过: " + skipped);
            writer.println("通过率: " + String.format("%.2f%%", (double)passed/testReports.size()*100));
            writer.println();
            
            // 测试用例详情
            writer.println("=== 测试用例详情 ===");
            for (TestReport report : testReports.values()) {
                writer.println("测试名称: " + report.getTestName());
                writer.println("状态: " + report.getStatus());
                writer.println("执行时间: " + report.getExecutionTime() + "ms");
                if (report.getErrorMessage() != null) {
                    writer.println("错误信息: " + report.getErrorMessage());
                }
                writer.println("开始时间: " + DATE_FORMAT.format(new Date(report.getStartTime())));
                writer.println("结束时间: " + DATE_FORMAT.format(new Date(report.getEndTime())));
                writer.println("---");
            }
            
            writer.close();
            
        } catch (IOException e) {
            System.err.println("生成文本报告失败: " + e.getMessage());
        }
    }
    
    private static class TestReport {
        private String testName;
        private String status;
        private long startTime;
        private long endTime;
        private long executionTime;
        private String errorMessage;
        
        public TestReport(String testName) {
            this.testName = testName;
        }
        
        // Getter和Setter方法
        public String getTestName() { return testName; }
        public void setTestName(String testName) { this.testName = testName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}