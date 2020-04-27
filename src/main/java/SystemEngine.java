import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class SystemEngine {

    /**
     * Create initial population. Store it in Variables class.
     */

    protected double optimistAllTotalPayoff = 0.0;
    protected double pessimistAllTotalPayoff = 0.0;
    protected double realistAllTotalPayoff = 0.0;
    protected double nonAdviceAllTotalPayoff = 0.0;

    protected double coopOptimistAllTotalPayoff = 0.0;
    protected double coopPessimistAllTotalPayoff = 0.0;
    protected double coopRealistAllTotalPayoff = 0.0;
    protected double coopNonAdviceAllTotalPayoff = 0.0;
    protected double defOptimistAllTotalPayoff = 0.0;
    protected double defPessimistAllTotalPayoff = 0.0;
    protected double defRealistAllTotalPayoff = 0.0;
    protected double defNonAdviceAllTotalPayoff = 0.0;

    private double coopOptimistMean = 0.0;
    private double coopPessimistMean = 0.0;
    private double coopRealistMean = 0.0;
    private double coopNoAdviceMean = 0.0;
    private double defOptimistMean = 0.0;
    private double defPessimistMean = 0.0;
    private double defRealistMean = 0.0;
    private double defNoAdviceMean = 0.0;

    private double totalMean = 0.0;
    private double totalPayoff = 0.0;

    private SystemEngine() {
    }

    public static void main(String[] args) {
        System.out.println("System Engine works");
        SystemEngine engine = new SystemEngine();
        try {
            engine.multiple_runs();
            engine.createCSVFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        Game game = new Game(); // Start with the same initial population
        game.life();
        coopOptimistAllTotalPayoff += game.coopOptimistTotalPayoff;
        coopPessimistAllTotalPayoff += game.coopPessimistTotalPayoff;
        coopRealistAllTotalPayoff += game.coopRealistTotalPayoff;
        coopNonAdviceAllTotalPayoff += game.coopNoAdviceTotalPayoff;
        defOptimistAllTotalPayoff += game.defOptimistTotalPayoff;
        defPessimistAllTotalPayoff += game.defPessimistTotalPayoff;
        defRealistAllTotalPayoff += game.defRealistTotalPayoff;
        defNonAdviceAllTotalPayoff += game.defNoAdviceTotalPayoff;
        calculateMemoryUsage(game.population);
    }

    private void multiple_runs() throws IOException {
        for (int w = 0; w < SystemVariables.worlds; w++) {
            run();
        }
        calculateMean();
    }

    public void calculateMean() {
        coopOptimistMean = coopOptimistAllTotalPayoff / (SystemVariables.worlds * 50);
        coopPessimistMean = coopPessimistAllTotalPayoff / (SystemVariables.worlds * 50);
        coopRealistMean = coopRealistAllTotalPayoff / (SystemVariables.worlds * 50);
        coopNoAdviceMean = coopNonAdviceAllTotalPayoff / (SystemVariables.worlds * 50);

        defOptimistMean = defOptimistAllTotalPayoff / (SystemVariables.worlds * 50);
        defPessimistMean = defPessimistAllTotalPayoff / (SystemVariables.worlds * 50);
        defRealistMean = defRealistAllTotalPayoff / (SystemVariables.worlds * 50);
        defNoAdviceMean = defNonAdviceAllTotalPayoff / (SystemVariables.worlds * 50);

        totalPayoff = coopOptimistAllTotalPayoff + coopPessimistAllTotalPayoff + coopRealistAllTotalPayoff
                + coopNonAdviceAllTotalPayoff
                + defOptimistAllTotalPayoff + defPessimistAllTotalPayoff + defRealistAllTotalPayoff
                + defNonAdviceAllTotalPayoff;
        totalPayoff = totalPayoff / SystemVariables.worlds;
        totalMean = totalPayoff / SystemVariables.N;
    }

    public void calculateMemoryUsage(Agent[] population) {
        double totalCoopInMem = 0;
        double totalDefInMem = 0;
        int count = 0;
        for (int i = 0; i < population.length; i++) {
            if ( SystemVariables.memRatio > 0) {
                double coopInMemRatio  =
                        population[i].memory.cooperatorsInMemory.size() / (population.length * SystemVariables.memRatio);

                double defInMemRatio =
                        population[i].memory.defectorsInMemory.size() / (population.length * SystemVariables.memRatio);
                totalCoopInMem += coopInMemRatio;
                totalDefInMem += defInMemRatio;
                count++;
            }
        }
        SystemVariables.averageCoopInMem = totalCoopInMem / count;
        SystemVariables.averageDefInMem = totalDefInMem / count;
    }

    public void createCSVFile() throws IOException {

        String csvFile = SystemVariables.path + "Rea.csv";
        FileWriter writer = new FileWriter(csvFile, true);

        if (SystemVariables.memRatio == 0) {
            CSVUtils.writeLine(writer, Arrays.asList("Memory Ratio",
                    "Coop Optimist",
                    "Coop Pessimist",
                    "Coop Realist",
                    "Coop No Advice",
                    "Def No Advice",
                    "Coop Optimist Ratio",
                    "Coop Pessimist Ratio",
                    "Coop Realist Ratio",
                    "Coop No Advice Ratio",
                    "Def Optimist Ratio",
                    "Def Pessimist Ratio",
                    "Def Realist Ratio",
                    "Def No Advice Ratio",
                    "# of Advice Res",
                    "# of Correct Advice",
                    "#CC_FromAdvisor",
                    "#DD_FromAdvisor",
                    "#DC_FromAdvisor",
                    "#CD_FromAdvisor",
                    "# of Correct Inference",
                    "#CC",
                    "#DD",
                    "#DC",
                    "#CD",
                    "# of tekAdvisorCoopuDefSayisi",
                    "# of tekAdvisorDefiCoopSayisi",
                    "Memory Usage",
                    "CoopInMem",
                    "DefInMem",
                    "coopla oyna",
                    "defle oyna",
                    "coopla oynama",
                    "defle oynama",
                    "# of Advice Res"
            ));
        }

        CSVUtils.writeLine(writer, Arrays.asList(String.valueOf(SystemVariables.memRatio),
                String.valueOf(coopOptimistMean),
                String.valueOf(coopPessimistMean),
                String.valueOf(coopRealistMean),
                String.valueOf(coopNoAdviceMean),
                String.valueOf(defNoAdviceMean),
                String.valueOf(coopOptimistMean / totalMean),
                String.valueOf(coopPessimistMean / totalMean),
                String.valueOf(coopRealistMean / totalMean),
                String.valueOf(coopNoAdviceMean / totalMean),
                String.valueOf(defOptimistMean / totalMean),
                String.valueOf(defPessimistMean / totalMean),
                String.valueOf(defRealistMean / totalMean),
                String.valueOf(defNoAdviceMean / totalMean),
                String.valueOf(SystemVariables.cevaplitavsiyeAlmaSayisi),
                String.valueOf(SystemVariables.dogruTavsiyeAlmaSayisi),
                String.valueOf(SystemVariables.cooperatoruCoopSanmaTavsiyeden),
                String.valueOf(SystemVariables.defectoruDefSanmaTavsiyeden),
                String.valueOf(SystemVariables.defectoruCoopSanmaTavsiyeden),
                String.valueOf(SystemVariables.cooperatoruDefSanmaTavsiyeden),
                String.valueOf(SystemVariables.dogruKendiCikarimiSayisi),
                String.valueOf(SystemVariables.cooperatoruCoopSanmaSayisi),
                String.valueOf(SystemVariables.defectoruDefSanmaSayisi),
                String.valueOf(SystemVariables.defectoruCoopSanmaSayisi),
                String.valueOf(SystemVariables.cooperatoruDefSanmaSayisi),
                String.valueOf(SystemVariables.tekAdvisorCoopuDefSayisi),
                String.valueOf(SystemVariables.tekAdvisorDefiCoopSayisi),
                String.valueOf(SystemVariables.averageCoopInMem + SystemVariables.averageDefInMem),
                String.valueOf(SystemVariables.averageCoopInMem),
                String.valueOf(SystemVariables.averageDefInMem),
                String.valueOf(SystemVariables.cooplaOyna),
                String.valueOf(SystemVariables.defleOyna),
                String.valueOf(SystemVariables.cooplaOynama),
                String.valueOf(SystemVariables.defleOynama),
                String.valueOf(SystemVariables.tavsiyeSayisi)

                ));

        writer.flush();
        writer.close();
    }
}