package com.alibou.book.DTO;

import java.math.BigDecimal;

public class DeliveryAssignmentRequest {
    private String pickupLocation;
    private String destinationAddress;
    private String animalType;
    private String quantityOrWeight;
    private String specialRequirements;
    private BigDecimal agreedCharge;

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }
    public String getAnimalType() { return animalType; }
    public void setAnimalType(String animalType) { this.animalType = animalType; }
    public String getQuantityOrWeight() { return quantityOrWeight; }
    public void setQuantityOrWeight(String quantityOrWeight) { this.quantityOrWeight = quantityOrWeight; }
    public String getSpecialRequirements() { return specialRequirements; }
    public void setSpecialRequirements(String specialRequirements) { this.specialRequirements = specialRequirements; }
    public BigDecimal getAgreedCharge() { return agreedCharge; }
    public void setAgreedCharge(BigDecimal agreedCharge) { this.agreedCharge = agreedCharge; }
}
