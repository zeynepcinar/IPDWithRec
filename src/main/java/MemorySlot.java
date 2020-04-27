public class MemorySlot {

    /**
     * Opponent ID
     */
    public Agent opponent;
    /**
     * Perceived defection rate of the opponent
     */
    public double RateOfDefection = 0.0;
    /**
     * Perceived cooperation rate of the opponent
     */
    public double RateOfCooperation = 0.0;
    /**
     * Number of plays with the same opponent
     */
    public int totalNumberOfPlays = 0;

    /**
     * How many times opponent has defected so far
     */
    public double numberOfPerceivedDefections = 0.0;        //
    /**
     * How many times opponent has defected so far
     */
    public double numberOfPerceivedCooperations = 0.0;        //

    /**
     * This memory cell is dedicated the particular opponent o
     *
     * @param o
     */
    public MemorySlot(Agent o) {
        opponent = o;
    }

    /**
     * Memory cell is dedicated one particular opponent. If it has cooperate in the current round, increase its
     * cooperation rate We use beta reputation function
     */

    public void increase_RateOfCooperation() {
        totalNumberOfPlays++;
        numberOfPerceivedCooperations++;
        RateOfCooperation = (numberOfPerceivedCooperations + 1) / (totalNumberOfPlays + 2);
        RateOfDefection = 1 - RateOfCooperation;
    }

    /**
     * Memory cell is dedicated one particular opponent. If it has defect in the current round, decrease its cooperation
     * rate We use beta reputation function
     */
    public void decrease_RateOfCooperation() {
        totalNumberOfPlays++;
        numberOfPerceivedDefections++;
        RateOfCooperation = (numberOfPerceivedCooperations + 1) / (totalNumberOfPlays + 2);
        RateOfDefection = 1 - RateOfCooperation;
    }

    /**
     * Memory cell is dedicated one particular opponent. If it has defect in the current round, decrease its cooperation
     * rate We use beta reputation function
     */
    public void set_RateOfCooperation(double cooperationRateGivenByAdvisor) {
        RateOfCooperation = cooperationRateGivenByAdvisor;
    }
}