package com.apex.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Academic grading utility for Apex University of Technology.
 * 10-Point Grading System:
 * 90 - 100 : Outstanding (O) -> Grade Point 10
 * 80 - 89.9: Excellent (A+)   -> Grade Point 9
 * 70 - 79.9: Very Good (A)   -> Grade Point 8
 * 60 - 69.9: Good (B+)        -> Grade Point 7
 * 50 - 59.9: Above Average(B) -> Grade Point 6
 * 40 - 49.9: Pass (P)         -> Grade Point 5
 * Below 40 : Fail (F)         -> Grade Point 0
 */
public class GradeCalculator {

    public static class GradeInfo {
        private final String letterGrade;
        private final double gradePoint;
        private final boolean isPass;

        public GradeInfo(String letterGrade, double gradePoint, boolean isPass) {
            this.letterGrade = letterGrade;
            this.gradePoint = gradePoint;
            this.isPass = isPass;
        }

        public String getLetterGrade() {
            return letterGrade;
        }

        public double getGradePoint() {
            return gradePoint;
        }

        public boolean isPass() {
            return isPass;
        }
    }

    public static GradeInfo calculateSubjectGrade(double totalMarks, double maxMarks) {
        if (maxMarks <= 0) {
            return new GradeInfo("F", 0.0, false);
        }
        double percentage = (totalMarks / maxMarks) * 100.0;

        if (percentage >= 90.0) {
            return new GradeInfo("O", 10.0, true);
        } else if (percentage >= 80.0) {
            return new GradeInfo("A+", 9.0, true);
        } else if (percentage >= 70.0) {
            return new GradeInfo("A", 8.0, true);
        } else if (percentage >= 60.0) {
            return new GradeInfo("B+", 7.0, true);
        } else if (percentage >= 50.0) {
            return new GradeInfo("B", 6.0, true);
        } else if (percentage >= 40.0) {
            return new GradeInfo("P", 5.0, true);
        } else {
            return new GradeInfo("F", 0.0, false);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
