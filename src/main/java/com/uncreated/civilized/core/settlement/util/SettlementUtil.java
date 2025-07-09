package com.uncreated.civilized.core.settlement.util;

import com.uncreated.civilized.core.settlement.Settlement;
import com.uncreated.civilized.core.villagerinfo.VillagerInfo;
import com.uncreated.civilized.core.villagerinfo.VillagerStore;

import java.util.List;

public class SettlementUtil {
    public static List<VillagerInfo> getCitizens(Settlement settlement, VillagerStore store) {
        return store.all().stream().filter(v -> settlement.getSettlementId().equals(v.getSettlementId())).toList();
    }
}
