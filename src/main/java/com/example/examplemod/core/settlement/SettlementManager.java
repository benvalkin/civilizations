package com.example.examplemod.core.settlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SettlementManager {

    public static final SettlementManager INSTANCE = new SettlementManager();

    private final List<Settlement> settlements;

    public SettlementManager() {
        settlements = new ArrayList<>();
    }

    public void loadSettlements() {

    }

    public void createNewSettlement() {
    }

    public Settlement getSettlement(int settlementId) {
        return settlements.stream().filter(f -> f.getSettlementId() == settlementId).findFirst().orElseThrow();
    }
    public Optional<Settlement> findSettlement(int settlementId) {
        return settlements.stream().filter(f -> f.getSettlementId() == settlementId).findFirst();
    }
}
