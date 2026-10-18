package pt.ipp.isep.dei.LAPR3.services.impl;

import pt.ipp.isep.dei.LAPR3.domain.*;

public class SpeedCalculationServiceImpl {

    private static final double MIN_SPEED = 20;  // segurança mínima
    private static final double POWER_FACTOR = 45; // fator de potência/peso

    /**
     * Calcula a velocidade efetiva num segmento
     */
    public double calculateEffectiveSpeed(Train train, LineSegment segment) {

        double totalPower = 0;
        double totalWeight = 0;
        double minOperationalSpeed = Double.MAX_VALUE;

        // --- Locomotivas ---
        for (Locomotive loco : train.getLocomotives()) {

            totalPower += loco.getLocomotiveModelID().getPower();

            // peso da locomotiva
            totalWeight += loco.getLocomotiveModelID()
                    .getVehicle()
                    .getWeight();

            // menor velocidade operacional entre locomotivas
            minOperationalSpeed = Math.min(minOperationalSpeed, loco.getOperationalSpeed());
        }

        // --- Freights + wagons ---
        for (Freight freight : train.getFreights()) {
            for (Wagon wagon : freight.getWagons()) {
                // tara
                totalWeight += wagon.wagonModel().getTareTons();
                // carga
                totalWeight += wagon.getPayloadTons();
            }
        }

        // --- Velocidade baseada na potência/peso ---
        double powerSpeed = (totalPower / totalWeight) * POWER_FACTOR;

        // --- Limite do segmento (linha) ---
        double trackLimit = segment.getSpeedLimit() > 0 ? segment.getSpeedLimit() : Double.MAX_VALUE;

        // --- Limite operacional baseado na locomotiva mais lenta ---
        double locoLimit = minOperationalSpeed;

        // --- Determinar velocidade final ---
        double effectiveSpeed = Math.min(powerSpeed, Math.min(trackLimit, locoLimit));

        // --- Garantir velocidade mínima de segurança ---
        return Math.max(MIN_SPEED, effectiveSpeed);
    }
}
