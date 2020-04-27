
import lombok.Getter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Getter
public class Agent {

    protected Memory memory;                                     // Memory to store past experience
    protected int identity;                                         // identity
    protected int memorySize;                                 // memory size
    protected double payoff;                                  // sum of payoffs updated after each round
    protected Characteristic agentCharacter;                  // traits related to cooperation probability, memory size
    protected MemorySlot currentOpponent;                     // memory cell for the current opponent
    protected String myAction = "";                          // Agent's action
    protected String opponentAction = "";                    // Opponent's action
    protected LinkedHashMap<Integer, Integer> opponents = new LinkedHashMap<>();               // Number of interaction with opponents
    protected LinkedHashMap<Integer, Integer> playedOpponents = new LinkedHashMap<>();         // Number of plays with opponents

    public Agent(int id, Characteristic character, int populationNumber) {
        payoff = 0.0;
        identity = id;
        agentCharacter = character;
        memorySize = (int) (character.memorySizeRatio * populationNumber);
        if (memorySize > 0) {
            memory = new Memory(memorySize, agentCharacter.disposition);
        }
    }

    /**
     * Refusal to play with defectors. Interaction rule.
     *
     * @param opponent
     * @return
     */


    public boolean randomPlay(Agent opponent) {
        Random r = new Random();
        int choice = r.nextInt(2);
        if(choice==0){
            if(agentCharacter.cooperationProbability < 0.5) {
                if (opponent.agentCharacter.cooperationProbability >= 0.5) {
                    SystemVariables.cooplaOynama++;
                } else {
                    SystemVariables.defleOynama++;
                }
            }
            return false;
        } else {
            if(agentCharacter.cooperationProbability < 0.5) {
                if (opponent.agentCharacter.cooperationProbability >= 0.5) {
                    SystemVariables.cooplaOyna++;
                } else {
                    SystemVariables.defleOyna++;
                }
            }
            return true;
        }
    }

    public boolean decisionToInteract(Agent opponent) {
        if (memorySize == 0) {
             return randomPlay(opponent);
            //return true;
         }
        currentOpponent = this.memory.rememberOpponent(opponent);
        if (currentOpponent != null) {
            if(this.agentCharacter.cooperationProbability >= 0.5) {
                if (opponent.getAgentCharacter().cooperationProbability < 0.5
                        && currentOpponent.RateOfCooperation >= 0.5) {
                    SystemVariables.defectoruCoopSanmaSayisi++;
                } else if (opponent.getAgentCharacter().cooperationProbability >= 0.5
                        && currentOpponent.RateOfCooperation < 0.5) {
                    SystemVariables.cooperatoruDefSanmaSayisi++;
                } else if (opponent.getAgentCharacter().cooperationProbability < 0.5
                        && currentOpponent.RateOfCooperation < 0.5) {
                    SystemVariables.defectoruDefSanmaSayisi++;
                    SystemVariables.dogruKendiCikarimiSayisi++;
                } else if (opponent.getAgentCharacter().cooperationProbability >= 0.5
                        && currentOpponent.RateOfCooperation >= 0.5) {
                    SystemVariables.cooperatoruCoopSanmaSayisi++;
                    SystemVariables.dogruKendiCikarimiSayisi++;
                }
            }
            // True means Cooperator (False means Defector)
            return currentOpponent.RateOfCooperation > SystemVariables.threshold;
        } else {
            Map<Integer, MemorySlot> advisorAgents = new LinkedHashMap<>();
            if (!agentCharacter.disposition.equals("NOADVICE")) {
                SystemVariables.tavsiyeSayisi++;
                advisorAgents = memory.searchForAdvisorNeighbors(opponent);
            }
            // if there is no agent to advise us about opponent or our agent is not search for recommendation, we choose to interact.
            if (advisorAgents.size() == 0) {
                  return randomPlay(opponent);
                //  return true;
            } else {
                SystemVariables.cevaplitavsiyeAlmaSayisi++;
                double trustValuesOfOpponent = evaluateRecommendations();
                if(this.agentCharacter.cooperationProbability >= 0.5) {
                    if (opponent.getAgentCharacter().cooperationProbability < 0.5 && trustValuesOfOpponent >= 0.5)                   {
                        SystemVariables.defectoruCoopSanmaTavsiyeden++;
                    } else if (opponent.getAgentCharacter().cooperationProbability >= 0.5
                            && trustValuesOfOpponent < 0.5) {
                        SystemVariables.cooperatoruDefSanmaTavsiyeden++;
                    } else if (opponent.getAgentCharacter().cooperationProbability < 0.5
                            && trustValuesOfOpponent < 0.5) {
                        SystemVariables.defectoruDefSanmaTavsiyeden++;
                        SystemVariables.dogruTavsiyeAlmaSayisi++;
                    } else if (opponent.getAgentCharacter().cooperationProbability >= 0.5
                            && trustValuesOfOpponent >= 0.5) {
                        SystemVariables.cooperatoruCoopSanmaTavsiyeden++;
                        SystemVariables.dogruTavsiyeAlmaSayisi++;
                    }
                }

                return trustValuesOfOpponent >= SystemVariables.referralThreshold;
            }
        }
    }

    /**
     * Cooperate with probability p or defect with (1-p)
     *
     * @return my_action : string that indicates action (cooperate or defect)
     */
    public String play() {
        double random = Math.random();
        if (random > agentCharacter.cooperationProbability) {
            myAction = "D";
        } else {
            myAction = "C";
        }
        return myAction;
    }

    /**
     * Evaluate the action of the opponent. Update its defection rate and category(defector or cooperator).
     *
     * @param opponent
     * @param opponentAction
     */
    public void reciprocate(Agent opponent, String opponentAction) {
        this.opponentAction = opponentAction;
        if (memorySize > 0) {
            memory.reciprocate(opponent, opponentAction);
        }
    }

    /**
     * Payoff depends on the joint actions.
     */
    public void updatePayoff() {
        String round = myAction + opponentAction;
        if ("CC".equals(round)) {
            this.payoff += SystemVariables.reward;
            return;
        }
        if ("CD".equals(round)) {
            this.payoff += SystemVariables.sucker;
            return;
        }
        if ("DC".equals(round)) {
            this.payoff += SystemVariables.temptation;
            return;
        }
        if ("DD".equals(round)) {
            this.payoff += SystemVariables.punishment;
            return;
        }
        System.out.println("\t\t---\t\t++++\t\t--- PROBLEM in updatePayoff");
    }

    public String toFile() {
        return this.agentCharacter.toFile() + " and this payoff = " + this.payoff;
    }

    public double giveReferral(int referencedAgentID) {
        return memory.allNeighbors.get(referencedAgentID).RateOfCooperation;
    }

    public double evaluateRecommendations() {
        String agentDisposition = agentCharacter.disposition;
        // Each different disposition results in different trust evaluations from an agent.
        // if agent is optimist
        if (agentDisposition.equals("OPTIMIST")) {
            return Collections.max(memory.trustValuesGivenByAdvisorAgents);
            // if agent is pessimist
        } else if (agentDisposition.equals("PESSIMIST")) {
            return Collections.min(memory.trustValuesGivenByAdvisorAgents);
            // if agent is realist
        } else if (agentDisposition.equals("REALIST_MEAN")) {
            return calculateMean(memory.trustValuesGivenByAdvisorAgents);
        } else if (agentDisposition.equals("REALIST_MODE")) {
            return calculateMode(memory.trustValuesGivenByAdvisorAgents);
        } else {
            return calculateMedian(memory.trustValuesGivenByAdvisorAgents);
        }
    }

    private double calculateMean(List<Double> trustValues) {
        Double sum = 0.0;
        if (!trustValues.isEmpty()) {
            for (Double trustValue : trustValues) {
                sum += trustValue;
            }
            return sum.doubleValue() / trustValues.size();
        }
        return sum;
    }

    private double calculateMode(List<Double> trustValues) {
        double maxValue = trustValues.get(0);
        int maxCount = 0;
        for (int i = 0; i < trustValues.size(); i++) {
            int count = 0;
            for (int j = 0; j < trustValues.size(); j++) {
                if (trustValues.get(j).doubleValue() == trustValues.get(i).doubleValue()) {
                    count++;
                }
            }
            if (count >= maxCount) {
                maxCount = count;
                maxValue = trustValues.get(i);
            }
        }

        return maxValue;
    }

    private double calculateMedian(List<Double> trustValues) {
        Collections.sort(trustValues);
//        if(trustValues.size()>4){
//            int a=0;
//        }
        if (trustValues.size() % 2 == 0) {
            return (trustValues.get(trustValues.size() / 2) + trustValues.get(trustValues.size() / 2 - 1)) / 2;
        } else {
            return trustValues.get(trustValues.size() / 2);
        }
    }
}