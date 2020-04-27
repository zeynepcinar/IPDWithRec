import enums.Disposition;

public class Characteristic {

    protected double cooperationProbability;                // Probability of Cooperation
    protected double memorySizeRatio;                       // Memory size ratio (same for every agent)
    protected String disposition;                           // Optimist & Pessimist & Realist

    public Characteristic(double cooperationProbability, double memorySizeRatio, double disposition) {
        this.cooperationProbability = cooperationProbability;
        this.memorySizeRatio = memorySizeRatio;
        this.disposition = dispositionGenerator(disposition);
    }

    public String dispositionGenerator(double dispositionEnum) {
        if (dispositionEnum == 0) {
            return Disposition.OPTIMIST.name();
        } else if (dispositionEnum == 1) {
            return Disposition.PESSIMIST.name();
        } else if (dispositionEnum == 2) {
            return Disposition.REALIST.name();
        } else {
            return Disposition.NOADVICE.name();
        }
    }

    public String toFile() {
        return "" + cooperationProbability + " " + memorySizeRatio + " " + disposition;
    }
}