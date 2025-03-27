public interface OrderInfoService {
    boolean processMonthlyAIInspection(Integer year, Integer month);
    boolean checkMonthlyDuplicates(Integer year, Integer month);
} 