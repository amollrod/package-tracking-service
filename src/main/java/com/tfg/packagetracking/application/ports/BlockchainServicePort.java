package com.tfg.packagetracking.application.ports;

import com.tfg.packagetracking.domain.models.PackageHistoryEvent;
import java.util.List;

public interface BlockchainServicePort {
    List<PackageHistoryEvent> getPackageHistory(String packageId);
}
