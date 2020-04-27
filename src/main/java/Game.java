import java.util.Random;

public class Game {

    protected double coopOptimistTotalPayoff = 0.0;
    protected double coopPessimistTotalPayoff = 0.0;
    protected double coopRealistTotalPayoff = 0.0;
    protected double coopNoAdviceTotalPayoff = 0.0;
    protected double defOptimistTotalPayoff = 0.0;
    protected double defPessimistTotalPayoff = 0.0;
    protected double defRealistTotalPayoff = 0.0;
    protected double defNoAdviceTotalPayoff = 0.0;
    protected double optimistTotalPayoff = 0.0;
    protected double pessimistTotalPayoff = 0.0;
    protected double realistTotalPayoff = 0.0;
    protected double noAdviceTotalPayoff = 0.0;
    protected Agent[] population;   // Population of agents
    private double[] socialWelfare; // Their social welfare (gathered payoffs)

    /**
     * Start with the initial population stored in the Variables class.
     */

    public Game() {
        population = new Agent[SystemVariables.N];
        createSpecialInitialPopulation();
        population = SystemVariables.initial_population;
        // initialPopulationToScreen();
    }

    public static void info(Agent player1, Agent player2) {
        int p1 = player1.getIdentity();
        int p2 = player2.getIdentity();
        if (player1.memory != null) {
            System.out.println("\t\t\t Agent " + p1 + "s memory: " + player1.memory.toScreenAllOpponentsInMemory()
                    + " \t and " + p1 + "s Payoff= " + player1.getPayoff());
        } else {
            System.out.println("\t\t\t Agent " + p1 + "s memory: NONE"
                    + " \t and " + p1 + "s Payoff= " + player1.getPayoff());
        }
        if (player2.memory != null) {
            System.out.println("\t\t\t Agent " + p2 + "s memory: " + player2.memory.toScreenAllOpponentsInMemory()
                    + " \t and " + p2 + "s Payoff= " + player2.getPayoff());
        } else {
            System.out.println("\t\t\t Agent " + p2 + "s memory: NONE"
                    + " \t and " + p2 + "s Payoff= " + player2.getPayoff());
        }


    }

    /**
     * Create initial population which optimist, pessimist and realist have equal numbers. Half of the population is
     * cooperator and other half is defector.
     */
    public static void createSpecialInitialPopulation() {
        SystemVariables.initial_population = new Agent[SystemVariables.N];
        for (int i = 0; i < SystemVariables.N; i++) {
            if (i < 50) {
                Characteristic trait = new Characteristic(0.9, SystemVariables.memRatio, 2);
                SystemVariables.initial_population[i] = new Agent(i, trait, SystemVariables.N);
            } else if (i >= 50) {
                Characteristic trait = new Characteristic(0.1, SystemVariables.memRatio, 3);
                SystemVariables.initial_population[i] = new Agent(i, trait, SystemVariables.N);
            }
        }
    }

    public void populationToScreen() {
        String cstr = "", dstr = "", mstr = "";
        for (int i = 0; i < SystemVariables.N; i++) {
            if (population[i].getAgentCharacter().cooperationProbability < SystemVariables.threshold) {
                cstr += population[i].identity + " ";
            } else {
                dstr += population[i].identity + " ";
            }
            mstr += population[i].memorySize + " ";
            System.out.println(
                    "Agent " + i + " (" + population[i].toFile() + ")\t\t(M = " + population[i].memorySize + ")" + "   "
                            + population[i].getAgentCharacter().disposition);
        }
    }

    public void initialPopulationToScreen() {
        String cstr = "", dstr = "", mstr = "";
        for (int i = 0; i < SystemVariables.N; i++) {
            if (SystemVariables.initial_population[i].getAgentCharacter().cooperationProbability
                    < SystemVariables.threshold) {
                cstr += SystemVariables.initial_population[i].identity + " ";
            } else {
                dstr += SystemVariables.initial_population[i].identity + " ";
            }
            mstr += SystemVariables.initial_population[i].memorySize + " ";
            System.out.println(
                    "Agent " + i + " (" + SystemVariables.initial_population[i].toFile() + ")\t\t(M = "
                            + SystemVariables.initial_population[i].memorySize + ")" + "   "
                            + SystemVariables.initial_population[i].getAgentCharacter().disposition);
        }
    }

    /**
     * Round of an attention game. Two distinct agents are selected. They can accept or refuse to play with each other.
     * They defect or cooperate depending on their defection gene. They evaluate the action of their opponent, place it
     * in either its cooperator list or defectors list in their memory. Finally they get a payoff depending on the joint
     * actions
     */
    public void round() {
        int p1, p2;
        Random rand = new Random();
        p1 = rand.nextInt(SystemVariables.N);
        do {
            p2 = rand.nextInt(SystemVariables.N);
        } while (p1 == p2);
       // System.out.println("\tP1 " + p1 + " P2 " + p2);

        // Two distinct agents are selected.
        Agent player1 = population[p1];
        Agent player2 = population[p2];
      

        updateInteractedOpponents(player1, player2);
        updateInteractedOpponents(player2, player1);

        boolean player1Decision = true;
        boolean player2Decision = true;

        player1Decision = player1.decisionToInteract(player2);
        player2Decision = player2.decisionToInteract(player1);

        if (!player1Decision  || !player2Decision) {
            return;
        }

        updatePlayedOpponents(player1, player2);
        updatePlayedOpponents(player2, player1);

        //******************//

        // They defect or cooperate depending on their cooperation gene
        String action1 = player1.play();
        String action2 = player2.play();
        // System.out.print("\t\tAgent " + p1 + " acted " + action1);
        // System.out.println(" and Agent " + p2 + " acted " + action2);

        // They evaluate the action of their opponent,
        // place it in either its cooperator list or defectors list in their memory
        player1.reciprocate(player2, action2);
        player2.reciprocate(player1, action1);
        //Finally they get a payoff depending on the joint actions

        player1.updatePayoff();
        player2.updatePayoff();
    }

    /**
     * Multiple rounds.
     */
    public void life() {
        for (int i = 0; i < SystemVariables.tau * (SystemVariables.N - 1) * (SystemVariables.N) / 2; i++) {
            round();
        }
        socialWelfare = getSocialWelfare();
    }

    /**
     * At the end of a rounds, return social walfare of agents.
     *
     * @return social walfare
     */
    public double[] getSocialWelfare() {

        socialWelfare = new double[population.length];

        for (int i = 0; i < SystemVariables.N; i++) {
            if (population[i].getAgentCharacter().disposition.equals("NOADVICE")
                    && population[i].getAgentCharacter().cooperationProbability >= 0.5) {
                coopNoAdviceTotalPayoff += population[i].getPayoff();
                noAdviceTotalPayoff += population[i].getPayoff();
            } else if (population[i].getAgentCharacter().disposition.equals("NOADVICE")
                    && population[i].getAgentCharacter().cooperationProbability < 0.5) {
                defNoAdviceTotalPayoff += population[i].getPayoff();
                noAdviceTotalPayoff += population[i].getPayoff();
            } else if (population[i].getAgentCharacter().disposition.equals("OPTIMIST")
                    && population[i].getAgentCharacter().cooperationProbability >= 0.5) {
                coopOptimistTotalPayoff += population[i].getPayoff();
                optimistTotalPayoff += population[i].getPayoff();
            } else if (population[i].getAgentCharacter().disposition.equals("OPTIMIST")
                    && population[i].getAgentCharacter().cooperationProbability < 0.5) {
                defOptimistTotalPayoff += population[i].getPayoff();
                optimistTotalPayoff += population[i].getPayoff();
            } else if (population[i].getAgentCharacter().disposition.equals("PESSIMIST")
                    && population[i].getAgentCharacter().cooperationProbability >= 0.5) {
                coopPessimistTotalPayoff += population[i].getPayoff();
                pessimistTotalPayoff += population[i].getPayoff();
            } else if (population[i].getAgentCharacter().disposition.equals("PESSIMIST")
                    && population[i].getAgentCharacter().cooperationProbability < 0.5) {
                defPessimistTotalPayoff += population[i].getPayoff();
                pessimistTotalPayoff += population[i].getPayoff();
            } else if (population[i].getAgentCharacter().disposition.equals("REALIST")
                    && population[i].getAgentCharacter().cooperationProbability >= 0.5) {
                coopRealistTotalPayoff += population[i].getPayoff();
                realistTotalPayoff += population[i].getPayoff();
            } else if (population[i].getAgentCharacter().disposition.equals("REALIST")
                    && population[i].getAgentCharacter().cooperationProbability < 0.5) {
                defRealistTotalPayoff += population[i].getPayoff();
                realistTotalPayoff += population[i].getPayoff();
            }
            socialWelfare[i] = population[i].getPayoff();
        }

        int maxAt = 0;
        for (int i = 0; i < socialWelfare.length; i++) {
            maxAt = socialWelfare[i] > socialWelfare[maxAt] ? i : maxAt;
        }
		
        return socialWelfare;
    }

    //deneme için silinicek
    private void updateInteractedOpponents(Agent player1, Agent player2) {
        if (!player1.opponents.containsKey(player2.getIdentity())) {
            player1.opponents.put(player2.getIdentity(), 1);
        } else {
            int numOfGamePlayWithAgent = player1.opponents.get(player2.getIdentity());
            numOfGamePlayWithAgent++;
            player1.opponents.put(player2.getIdentity(), numOfGamePlayWithAgent);
        }
    }

    private void updatePlayedOpponents(Agent player1, Agent player2) {
        if (!player1.playedOpponents.containsKey(player2.getIdentity())) {
            player1.playedOpponents.put(player2.getIdentity(), 1);
        } else {
            int numOfGamePlayWithAgent = player1.playedOpponents.get(player2.getIdentity());
            numOfGamePlayWithAgent++;
            player1.playedOpponents.put(player2.getIdentity(), numOfGamePlayWithAgent);
        }
    }

    private static double getRandomNumberInRange(double min, double max) {
        Random generator = new Random();
        return generator.nextDouble() * (max - min) + min;
    }
}