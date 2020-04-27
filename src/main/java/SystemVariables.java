import java.io.FileWriter;

/**
 * Variables and Parameters of the whole simulation.
 *
 * @author zeynepcinar
 */

public class SystemVariables {

    // Population sizeInFebruaryAllAdvisorForgerCoop
    protected static final int N = 100;
    // lifetime of a generation (tau x (N chooses 2))
    protected static final int tau = 30;
    //Memory ratio
    protected static double memRatio = 0.15
            ;
    // Number of parallel worlds with
    // identical initial conditions
    protected static final int worlds = 50;
    // Defector perception threshold fixed to 0.5
    protected static final double threshold = 0.5;
    protected static final double referralThreshold = 0.5;
    // Payoff matrix as environmental conditions
    protected static final double sucker = 0, punishment = 1, reward = 3, temptation = 5;
    // Initial population (stored for parallel worlds)
    protected static Agent[] initial_population;
    // Specify the file-name for storing output
    protected static String path = "Mart2020/";
    protected static final double recommendationDepth = 1;
    public static FileWriter globalWriter;

    public static int tavsiyeSayisi = 0;
    public static int cevaplitavsiyeAlmaSayisi = 0;
    public static int dogruTavsiyeAlmaSayisi = 0;
    public static int defectoruCoopSanmaTavsiyeden = 0;
    public static int cooperatoruDefSanmaTavsiyeden = 0;
    public static int defectoruDefSanmaTavsiyeden = 0;
    public static int cooperatoruCoopSanmaTavsiyeden = 0;
    public static int tekAdvisorCoopuDefSayisi = 0;
    public static int tekAdvisorDefiCoopSayisi = 0;


    public static int dogruKendiCikarimiSayisi = 0;
    public static int cooperatoruDefSanmaSayisi = 0;
    public static int defectoruCoopSanmaSayisi = 0;
    public static int cooperatoruCoopSanmaSayisi = 0;
    public static int defectoruDefSanmaSayisi = 0;

    public static double averageCoopInMem = 0;
    public static double averageDefInMem = 0;
    public static double averageMemRatio = 0;

    public static int cooplaOyna = 0;
    public static int defleOyna = 0;
    public static int cooplaOynama = 0;
    public static int defleOynama = 0;

    //    public static double[] memoryRatios = {0.3};
    private SystemVariables() {
    }

}