import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Memory {

    protected int capacity;
    protected double threshold;
    protected String disposition;
    protected MemorySlot currentOpponentInMemory;                         // MemoryCell for current interacting opponent
    protected LinkedHashMap<Integer, MemorySlot> currentList;                        // Current opponent belongs to which list
    // Memory is composed of two array lists
    protected LinkedHashMap<Integer, MemorySlot> cooperatorsInMemory = new LinkedHashMap<>();               // Clist for cooperators
    protected LinkedHashMap<Integer, MemorySlot> defectorsInMemory = new LinkedHashMap<>();                   // Dlist for defectors
    protected LinkedHashMap<Integer, MemorySlot> allNeighbors = new LinkedHashMap<>();
    protected LinkedHashMap<Integer, MemorySlot> advisorNeighbors = new LinkedHashMap<>();
    protected List<Double> trustValuesGivenByAdvisorAgents = new ArrayList<>();   // Recommendations given by advisor agents.


    public Memory(int memorySize, String characterDisposition) {
        capacity = memorySize;
        disposition = characterDisposition;
        threshold = SystemVariables.threshold;
    }

    public MemorySlot rememberOpponent(Agent opponent) {
        if (capacity == 0) {
            return null;
        }
        currentOpponentInMemory = null;
        currentList = null;

        if (defectorsInMemory.containsKey(opponent.getIdentity())) {
            currentOpponentInMemory = defectorsInMemory.get(opponent.getIdentity());
            currentList = defectorsInMemory;
            return currentOpponentInMemory;
        }

        if (cooperatorsInMemory.containsKey(opponent.getIdentity())) {
            currentOpponentInMemory = cooperatorsInMemory.get(opponent.getIdentity());
            currentList = cooperatorsInMemory;
            return currentOpponentInMemory;
        }
        return null;
    }

    /**
     * Evaluate the action of the current opponent AFTER playing with the other agent.
     *
     * @param opponent
     * @param opponentAction
     */
    public void reciprocate(Agent opponent, String opponentAction) {
        // current interacting opponent is NOT in the memory
        if (currentOpponentInMemory == null) {
            memorizeOpponent(opponent, opponentAction);
            return;
        }
        // current interacting opponent is in the memory
        if ("C".equals(opponentAction)) {
            currentOpponentInMemory.increase_RateOfCooperation();
        } else {
            currentOpponentInMemory.decrease_RateOfCooperation();
        }
        // Check for the right list
        this.checkForTheRightList();
    }

    /**
     * Learn an unknown opponent .. Evaluate its action and store in the right list Memory size restriction imposes the
     * need for forgetting .. a previously known opponent (prefer to forget defectors)
     *
     * @param opponent
     * @param opponentAction
     */
    public void memorizeOpponent(Agent opponent, String opponentAction) {
        MemorySlot newOpponent = new MemorySlot(opponent);
        // If it has defected in the first round, put it into Dlist
        if ("D".equals(opponentAction)) {
            newOpponent.decrease_RateOfCooperation();
            // Memory size restriction may impose the need for forgetting
            if (isFull()) {
                forgetPreferentiallyCooperators();
            }
            defectorsInMemory.put(opponent.getIdentity(), newOpponent);
            allNeighbors.put(opponent.getIdentity(), newOpponent);

        }
        // If it has cooperated in the first round, put it into Clist
        if ("C".equals(opponentAction)) {
            newOpponent.increase_RateOfCooperation();
            // Memory size restriction may impose the need for forgetting
            if (isFull()) {
                forgetPreferentiallyCooperators();
            }
            cooperatorsInMemory.put(opponent.getIdentity(), newOpponent);
            allNeighbors.put(opponent.getIdentity(), newOpponent);
        }
    }

    /**
     * Check for the right list. Previously perceived cooperator may exceed the defection threshold, thus it should be
     * removed from cooperator list and placed into defector list Previously perceived defector can not exceed the
     * defection threshold because of the refusal rule
     */
    public void checkForTheRightList() {
        if (currentOpponentInMemory.RateOfCooperation < threshold && currentList == cooperatorsInMemory) {
            cooperatorsInMemory.remove(currentOpponentInMemory.opponent.getIdentity());
            defectorsInMemory.put(currentOpponentInMemory.opponent.getIdentity(), currentOpponentInMemory);
        }
    }

    public boolean isFull() {
        if (cooperatorsInMemory.size() + defectorsInMemory.size() > capacity) {
            System.out.println("\n\n\n\n\n\n");
            System.out.println("\n\n\n\n\n\n");
            System.out.println("\n\n\n\n\n\n");
            System.out.println("BIG Problem:  I have " + (cooperatorsInMemory.size() + defectorsInMemory.size()) +
                    " items" + " in my memory WHILE my memory size is " + capacity);
            System.out.println("So I quit \n\n\n\n\n\n");
            System.exit(0);
        }
        return (capacity == cooperatorsInMemory.size() + defectorsInMemory.size());
    }

    //first in first out
    public void forgetCooperator() {
        Set<Integer> keySet = cooperatorsInMemory.keySet();
        List<Integer> keyList = new ArrayList<>(keySet);
        //int randomIndex = (int) (Math.random() * keySet.size());
        //int toRemovedKey = keyList.get(randomIndex);
        int toRemovedKey = keyList.get(0);
        //System.out.print("random index " + toRemovedKey);
        allNeighbors.remove(toRemovedKey);
        cooperatorsInMemory.remove(toRemovedKey);
    }

    // first in first out
    public void forgetDefector() {
        Set<Integer> keySet = defectorsInMemory.keySet();
        List<Integer> keyList = new ArrayList<>(keySet);
        //int randomIndex = (int) (Math.random() * keySet.size());
        //int toRemovedKey = keyList.get(randomIndex);
        int toRemovedKey = keyList.get(0);
        // System.out.print("random index " + toRemovedKey);
        allNeighbors.remove(toRemovedKey);
        defectorsInMemory.remove(toRemovedKey);
    }

    /**
     * Keep defectors (forget cooperators). Forget primarily Cooperators, if it is not possible to do so, than forget
     * defectors
     */
    public void forgetPreferentiallyCooperators() {
        // Try to forget cooperators
        if (!cooperatorsInMemory.isEmpty()) {
            forgetCooperator();
        } else if (!defectorsInMemory.isEmpty()) {
            forgetDefector();
        }
    }

    /**
     * Keep cooperators (forget defectors). Forget primarily Defectors, if it is not possible to do so, than forget
     * cooperators
     */
    public void forgetPreferentiallyDefectors() {
        // Try to forget cooperators
        if (!defectorsInMemory.isEmpty()) {
            forgetDefector();
        } else if (!cooperatorsInMemory.isEmpty()) {
            forgetCooperator();
        }
    }

    /**
     * Forget at random
     */
    public void randomForget() {
        // Try to forget cooperators
        //if (capacity == 0) return;
        int f = (int) (Math.random() * capacity);

        if (f < cooperatorsInMemory.size()) {
            forgetCooperator();
        } else {
            if (!defectorsInMemory.isEmpty()) {
                forgetDefector();
            } else {
                System.out.println("Problem in random forgetting");
                System.out.println(
                        cooperatorsInMemory.size() + " + " + defectorsInMemory.size() + " + " + capacity + " f " + f);
            }
        }
    }

    public String toScreenAllOpponentsInMemory() {
        String str = "C: ";
        Set<Integer> coopKeySet = cooperatorsInMemory.keySet();
        Set<Integer> defKeySet = defectorsInMemory.keySet();
        for (Integer key : coopKeySet) {
            str += cooperatorsInMemory.get(key).opponent.getIdentity() + " ";
        }
        str += " --- D: ";
        for (Integer key : defKeySet) {
            str += defectorsInMemory.get(key).opponent.getIdentity() + " ";
        }
        str += "(M = " + capacity + ")";
        return str;
    }

    public Map<Integer, MemorySlot> searchForAdvisorNeighbors(Agent opponent) {
        advisorNeighbors = new LinkedHashMap<>();
        trustValuesGivenByAdvisorAgents = new ArrayList<>();
        Set<Integer> allNeighborsKeySet = allNeighbors.keySet();
        for (Integer key : allNeighborsKeySet) {
            MemorySlot neighborInMemory = allNeighbors.get(key);
            LinkedHashMap<Integer, MemorySlot> friendsOfFriend = neighborInMemory.opponent.memory.allNeighbors;
            if (friendsOfFriend.containsKey(opponent.getIdentity())) {
                MemorySlot advisorNeighbor = neighborInMemory;
                advisorNeighbors.put(key, advisorNeighbor);
                trustValuesGivenByAdvisorAgents.add(advisorNeighbor.opponent.giveReferral(opponent.getIdentity()));
            }
        }

        if (advisorNeighbors.size() == 1 && opponent.getIdentity() < 50
                && trustValuesGivenByAdvisorAgents.get(0) < 0.5) {
            SystemVariables.tekAdvisorCoopuDefSayisi++;
        } else if (advisorNeighbors.size() == 1 && opponent.getIdentity() >= 50
                && trustValuesGivenByAdvisorAgents.get(0) >= 0.5) {
            SystemVariables.tekAdvisorDefiCoopSayisi++;
        }
        return advisorNeighbors;
    }
}