CREATE TABLE Building (
                          buildingID     number(10) CONSTRAINT NN_Building_buildingID NOT NULL,
                          facilityID     number(10) CONSTRAINT NN_Building_facilityID NOT NULL,
                          buildingTypeID number(10) CONSTRAINT NN_Building_buildingTypeID NOT NULL,
                          CONSTRAINT PK_Building PRIMARY KEY (buildingID));
CREATE TABLE BuildingType (
                              buildingTypeID number(10) CONSTRAINT NN_BuildingType_buildingTypeID NOT NULL,
                              name           varchar2(255) CONSTRAINT NN_BuildingType_name NOT NULL,
                              CONSTRAINT PK_BuildingType PRIMARY KEY (buildingTypeID));
CREATE TABLE DoubleTrack (
                             segmentID number(10) CONSTRAINT NN_DoubleTrack_segmentID NOT NULL,
                             CONSTRAINT PK_DoubleTrack PRIMARY KEY (segmentID));
CREATE TABLE Facility (
                          facilityID     number(10) CONSTRAINT NN_Facility_facilityID NOT NULL,
                          name           varchar2(100) CONSTRAINT NN_Facility_name NOT NULL CONSTRAINT UQ_Facility_name UNIQUE,
                          facilityTypeID number(10) CONSTRAINT NN_Facility_facilityTypeID NOT NULL,
                          CONSTRAINT PK_Facility PRIMARY KEY (facilityID));
CREATE TABLE FacilityType (
                              facilityTypeID number(10) CONSTRAINT NN_FacilityType_facilityTypeID NOT NULL,
                              name           varchar2(255) CONSTRAINT NN_FacilityType_name NOT NULL CONSTRAINT CK_FacilityType_name CHECK(name IN ('STATION','TERMINAL', 'FREIGHT_YARD')),
                              isIntermodal   char(1) CONSTRAINT NN_FacilityType_isIntermodal NOT NULL CONSTRAINT CK_FacilityType_isIntermodal CHECK(isIntermodal IN ('Y','N')),
                              CONSTRAINT PK_FacilityType PRIMARY KEY (facilityTypeID),
                              CONSTRAINT CHK_Facility_Intermodal
                                  CHECK ((name = 'TERMINAL' AND isIntermodal IN ('Y','N')) OR (name IN ('STATION', 'FREIGHT_YARD') AND isIntermodal = 'N') ));
CREATE TABLE Freight (
                         freightID       number(10) CONSTRAINT NN_Freight_freightID NOT NULL,
                         startFacilityID number(10) CONSTRAINT NN_Freight_startFacilityID NOT NULL,
                         endFacilityID   number(10) CONSTRAINT NN_Freight_endFacilityID NOT NULL,
                         "date"          timestamp(0) CONSTRAINT NN_Freight_date NOT NULL,
                         CONSTRAINT PK_Freight PRIMARY KEY (freightID));
CREATE TABLE Freight_Wagon (
                               wagonID   number(10) CONSTRAINT NN_FreightWagon_wagonID NOT NULL,
                               freightID number(10) CONSTRAINT NN_FreightWagon_freightID NOT NULL,
                               CONSTRAINT PK_FreightWagon PRIMARY KEY (wagonID,
                                            freightID));
CREATE TABLE Gauge (
                       dimension number(10) CONSTRAINT NN_Gauge_dimension NOT NULL,
                       name      varchar2(255) CONSTRAINT NN_Gauge_name NOT NULL,
                       CONSTRAINT PK_Gauge PRIMARY KEY (dimension));
CREATE TABLE Gauge_Vehicle (
                               dimension number(10) CONSTRAINT NN_GaugeVehicle_dimension NOT NULL,
                               vehicleID number(10) CONSTRAINT NN_GaugeVehicle_vehicleID NOT NULL,
                               CONSTRAINT PK_GaugeVehicle PRIMARY KEY (dimension,
                                            vehicleID));
CREATE TABLE Line (
                      lineID          number(10) CONSTRAINT NN_Line_lineID NOT NULL,
                      startFacilityID number(10) CONSTRAINT NN_Line_startFacilityID NOT NULL,
                      endFacilityID   number(10) CONSTRAINT NN_Line_endFacilityID NOT NULL,
                      lineOwnerID     number(10) CONSTRAINT NN_Line_lineOwnerID NOT NULL,
                      gaugeID         number(10) CONSTRAINT NN_Line_gaugeID NOT NULL,
                      isElectrified   char(1) CONSTRAINT NN_Line_isElectrified NOT NULL CONSTRAINT CK_Line_isElectrified CHECK(isElectrified IN ('Y','N')),
                      CONSTRAINT PK_Line PRIMARY KEY (lineID));
CREATE TABLE LineOwner (
                           lineOwnerID number(10) CONSTRAINT NN_LineOwner_lineOwnerID NOT NULL,
                           vatNumber   varchar2(255) CONSTRAINT NN_LineOwner_vatNumber NOT NULL CONSTRAINT UQ_LineOwner_vatNumber UNIQUE,
                           name        varchar2(100) CONSTRAINT NN_LineOwner_name NOT NULL,
                           CONSTRAINT PK_LineOwner PRIMARY KEY (lineOwnerID));
CREATE TABLE LineSegment (
                             segmentID         number(10) CONSTRAINT NN_LineSegment_segmentID NOT NULL,
                             lineID            number(10) CONSTRAINT NN_LineSegment_lineID NOT NULL,
                             orderLine         number(10) CONSTRAINT NN_LineSegment_orderLine NOT NULL,
                             maxWeight         number(10, 3) CONSTRAINT NN_LineSegment_maxWeight NOT NULL,
                             length            number(10, 3) CONSTRAINT NN_LineSegment_length NOT NULL,
                             maintenanceStatus char(1) CONSTRAINT NN_LineSegment_maintenanceStatus NOT NULL CONSTRAINT CK_LineSegment_maintenanceStatus CHECK(maintenanceStatus IN ('Y', 'N')),
                             speedLimit        number(10) CONSTRAINT NN_LineSegment_speedLimit NOT NULL,
                             CONSTRAINT PK_LineSegment PRIMARY KEY (segmentID));
CREATE TABLE Locomotive (
                            locomotiveID      number(10) CONSTRAINT NN_Locomotive_locomotiveID NOT NULL,
                            operatorID        number(10) CONSTRAINT NN_Locomotive_operatorID NOT NULL,
                            locomotiveModelID number(10) CONSTRAINT NN_Locomotive_locomotiveModelID NOT NULL,
                            makeId            number(10) CONSTRAINT NN_Locomotive_makeId NOT NULL,
                            startFacilityID   number(10) CONSTRAINT NN_Locomotive_startFacilityID NOT NULL,
                            name              varchar2(255),
                            serviceYear       number(10) CONSTRAINT NN_Locomotive_serviceYear NOT NULL,
                            operationalSpeed  number(10, 3) CONSTRAINT NN_Locomotive_operationalSpeed NOT NULL,
                            status            varchar2(255) CONSTRAINT NN_Locomotive_status NOT NULL CONSTRAINT CK_Locomotive_status CHECK(status IN ('ASSIGNED','AVAILABLE')),
                            CONSTRAINT PK_Locomotive PRIMARY KEY (locomotiveID));
CREATE TABLE LocomotiveMake (
                                makeId number(10) CONSTRAINT NN_LocomotiveMake_makeId NOT NULL,
                                name   varchar2(255) CONSTRAINT NN_LocomotiveMake_name NOT NULL,
                                CONSTRAINT PK_LocomotiveMake PRIMARY KEY (makeId));
CREATE TABLE LocomotiveModel (
                                 locomotiveModelID number(10) CONSTRAINT NN_LocomotiveModel_locomotiveModelID NOT NULL,
                                 vehicleID         number(10) CONSTRAINT NN_LocomotiveModel_vehicleID NOT NULL,
                                 power             number(10, 3) CONSTRAINT NN_LocomotiveModel_power NOT NULL,
                                 CONSTRAINT PK_LocomotiveModel PRIMARY KEY (locomotiveModelID));
CREATE TABLE LocomotiveModel_Diesel (
                                        locomotiveModelID number(10) CONSTRAINT NN_LocomotiveModelDiesel_locomotiveModelID NOT NULL,
                                        fuelCapacity      number(10) CONSTRAINT NN_LocomotiveModelDiesel_fuelCapacity NOT NULL,
                                        CONSTRAINT PK_LocomotiveModelDiesel PRIMARY KEY (locomotiveModelID));
CREATE TABLE LocomotiveModel_Electric (
                                          locomotiveModelID number(10) CONSTRAINT NN_LocomotiveModelElectric_locomotiveModelID NOT NULL,
                                          frequency         number(10) CONSTRAINT NN_LocomotiveModelElectric_frequency NOT NULL,
                                          voltage           number(10) CONSTRAINT NN_LocomotiveModelElectric_voltage NOT NULL,
                                          CONSTRAINT PK_LocomotiveModelElectric PRIMARY KEY (locomotiveModelID));
CREATE TABLE Operator (
                          operatorID number(10) CONSTRAINT NN_Operator_operatorID NOT NULL,
                          vatNumber  varchar2(255) CONSTRAINT NN_Operator_vatNumber NOT NULL CONSTRAINT UQ_Operator_vatNumber UNIQUE,
                          name       varchar2(100) CONSTRAINT NN_Operator_name NOT NULL,
                          CONSTRAINT PK_Operator PRIMARY KEY (operatorID));
CREATE TABLE Path (
                      pathID number(10) CONSTRAINT NN_Path_pathID NOT NULL,
                      CONSTRAINT PK_Path PRIMARY KEY (pathID));
CREATE TABLE Path_Facility (
                               pathID        number(10) CONSTRAINT NN_PathFacility_pathID NOT NULL,
                               facilityID    number(10) CONSTRAINT NN_PathFacility_facilityID NOT NULL,
                               "order"       number(10) CONSTRAINT NN_PathFacility_order NOT NULL,
                               arrivalTime   timestamp(0) CONSTRAINT NN_PathFacility_arrivalTime NOT NULL,
                               departureTime timestamp(0) CONSTRAINT NN_PathFacility_departureTime NOT NULL,
                               CONSTRAINT PK_PathFacility PRIMARY KEY (pathID,
                                            facilityID));
CREATE TABLE Path_Route (
                            pathID  number(10) CONSTRAINT NN_PathRoute_pathID NOT NULL,
                            routeID number(10) CONSTRAINT NN_PathRoute_routeID NOT NULL,
                            CONSTRAINT PK_PathRoute PRIMARY KEY (pathID,
                                         routeID));
CREATE TABLE Route (
                       routeID     number(10) CONSTRAINT NN_Route_routeID NOT NULL,
                       endPointAID number(10) CONSTRAINT NN_Route_endPointAID NOT NULL,
                       endPointBID number(10) CONSTRAINT NN_Route_endPointBID NOT NULL,
                       CONSTRAINT PK_Route PRIMARY KEY (routeID));
CREATE TABLE Siding (
                        sidingID  number(10) CONSTRAINT NN_Siding_sidingID NOT NULL,
                        segmentID number(10) CONSTRAINT NN_Siding_segmentID NOT NULL,
                        position  number(10) CONSTRAINT NN_Siding_position NOT NULL,
                        length    number(10) CONSTRAINT NN_Siding_length NOT NULL,
                        CONSTRAINT PK_Siding PRIMARY KEY (sidingID));
CREATE TABLE SimpleTrack (
                             segmentID number(10) CONSTRAINT NN_SimpleTrack_segmentID NOT NULL,
                             CONSTRAINT PK_SimpleTrack PRIMARY KEY (segmentID));
CREATE TABLE Train (
                       trainID   number(10) CONSTRAINT NN_Train_trainID NOT NULL,
                       routeID   number(10) CONSTRAINT NN_Train_routeID NOT NULL,
                       startDate timestamp(0) CONSTRAINT NN_Train_startDate NOT NULL,
                       maxLenght number(10) CONSTRAINT NN_Train_maxLenght NOT NULL,
                       status    varchar2(255) CONSTRAINT NN_Train_status NOT NULL CONSTRAINT CK_Train_status CHECK(status IN ('PLANNED','AVAILABLE', 'RUNNING')),
                       CONSTRAINT PK_Train_ PRIMARY KEY (trainID));
CREATE TABLE Train_Freight (
                               trainID   number(10) CONSTRAINT NN_TrainFreight_trainID NOT NULL,
                               freightID number(10) CONSTRAINT NN_TrainFreight_freightID NOT NULL CONSTRAINT UQ_TrainFreight_freightID UNIQUE,
                               CONSTRAINT PK_TrainFreight PRIMARY KEY (trainID,
                                            freightID));
CREATE TABLE Train_Locomotive (
                                  trainID      number(10) CONSTRAINT NN_TrainLocomotive_trainID NOT NULL,
                                  locomotiveID number(10) CONSTRAINT NN_TrainLocomotive_locomotiveID NOT NULL,
                                  CONSTRAINT PK_TrainLocomotive PRIMARY KEY (trainID,
                                               locomotiveID));
CREATE TABLE Vehicle (
                         vehicleID      number(10) CONSTRAINT NN_Vehicle_vehicleID NOT NULL,
                         name           varchar2(255) CONSTRAINT NN_Vehicle_name NOT NULL CONSTRAINT CK_Vehicle_name UNIQUE,
                         weight         number(10, 3) CONSTRAINT NN_Vehicle_weight NOT NULL,
                         width          number(10, 3) CONSTRAINT NN_Vehicle_width NOT NULL,
                         height         number(10, 3) CONSTRAINT NN_Vehicle_height NOT NULL,
                         length         number(10, 3) CONSTRAINT NN_Vehicle_length NOT NULL,
                         numberOfWheels number(10) CONSTRAINT NN_Vehicle_numberOfWheels NOT NULL,
                         maxSpeed       number(10) CONSTRAINT NN_Vehicle_maxSpeed NOT NULL,
                         CONSTRAINT PK_Vehicle PRIMARY KEY (vehicleID));
CREATE TABLE Wagon (
                       wagonID       number(10) CONSTRAINT NN_Wagon_wagonID NOT NULL,
                       operatorID    number(10) CONSTRAINT NN_Wagon_operatorID NOT NULL,
                       wagonModelID  number(10) CONSTRAINT NN_Wagon_wagonModelID NOT NULL,
                       startFacility number(10) CONSTRAINT NN_Wagon_startFacility NOT NULL,
                       serviceYear   number(10) CONSTRAINT NN_Wagon_serviceYear NOT NULL,
                       payloadTons   number(10, 3) CONSTRAINT NN_Wagon_payloadTons NOT NULL,
                       status        varchar2(255) CONSTRAINT NN_Wagon_status NOT NULL CONSTRAINT CK_Wagon_status CHECK(status IN ('ASSIGNED','AVAILABLE')),
                       CONSTRAINT PK_Wagon PRIMARY KEY (wagonID));
CREATE TABLE WagonMaker (
                            wagonMakerID number(10) CONSTRAINT NN_WagonMaker_wagonMakerID NOT NULL,
                            name         varchar2(255) CONSTRAINT NN_WagonMaker_name NOT NULL,
                            CONSTRAINT PK_WagonMaker PRIMARY KEY (wagonMakerID));
CREATE TABLE WagonModel (
                            wagonModelID number(10) CONSTRAINT NN_WagonModel_wagonModelID NOT NULL,
                            vehicleID    number(10) CONSTRAINT NN_WagonModel_vehicleID NOT NULL,
                            wagonTypeID  number(10) CONSTRAINT NN_WagonModel_wagonTypeID NOT NULL,
                            wagonMakerID number(10) CONSTRAINT NN_WagonModel_wagonMakerID NOT NULL,
                            tareTons     number(10, 3) CONSTRAINT NN_WagonModel_tareTons NOT NULL,
                            maxPayload   number(10) CONSTRAINT NN_WagonModel_maxPayload NOT NULL,
                            volume       number(10, 3) CONSTRAINT NN_WagonModel_volume NOT NULL,
                            CONSTRAINT PK_WagonModel PRIMARY KEY (wagonModelID));
CREATE TABLE WagonType (
                           wagonTypeID number(10) CONSTRAINT NN_WagonType_wagonTypeID NOT NULL,
                           name        varchar2(255) CONSTRAINT NN_WagonType_name NOT NULL,
                           CONSTRAINT CK_WagonType PRIMARY KEY (wagonTypeID));
ALTER TABLE Line ADD CONSTRAINT FKLine786860 FOREIGN KEY (lineOwnerID) REFERENCES LineOwner (lineOwnerID);
ALTER TABLE LineSegment ADD CONSTRAINT FKLineSegmen658834 FOREIGN KEY (lineID) REFERENCES Line (lineID);
ALTER TABLE Locomotive ADD CONSTRAINT FKLocomotive415770 FOREIGN KEY (operatorID) REFERENCES Operator (operatorID);
ALTER TABLE Wagon ADD CONSTRAINT FKWagon842233 FOREIGN KEY (operatorID) REFERENCES Operator (operatorID);
ALTER TABLE Line ADD CONSTRAINT FKLine158172 FOREIGN KEY (startFacilityID) REFERENCES Facility (facilityID);
ALTER TABLE Line ADD CONSTRAINT FKLine778880 FOREIGN KEY (endFacilityID) REFERENCES Facility (facilityID);
ALTER TABLE Locomotive ADD CONSTRAINT FKLocomotive84805 FOREIGN KEY (locomotiveModelID) REFERENCES LocomotiveModel (locomotiveModelID);
ALTER TABLE Locomotive ADD CONSTRAINT FKLocomotive90976 FOREIGN KEY (makeId) REFERENCES LocomotiveMake (makeId);
ALTER TABLE LocomotiveModel ADD CONSTRAINT FKLocomotive888922 FOREIGN KEY (vehicleID) REFERENCES Vehicle (vehicleID);
ALTER TABLE WagonModel ADD CONSTRAINT FKWagonModel444748 FOREIGN KEY (vehicleID) REFERENCES Vehicle (vehicleID);
ALTER TABLE LocomotiveModel_Diesel ADD CONSTRAINT FKLocomotive99720 FOREIGN KEY (locomotiveModelID) REFERENCES LocomotiveModel (locomotiveModelID);
ALTER TABLE LocomotiveModel_Electric ADD CONSTRAINT FKLocomotive791380 FOREIGN KEY (locomotiveModelID) REFERENCES LocomotiveModel (locomotiveModelID);
ALTER TABLE WagonModel ADD CONSTRAINT FKWagonModel5730 FOREIGN KEY (wagonTypeID) REFERENCES WagonType (wagonTypeID);
ALTER TABLE Line ADD CONSTRAINT FKLine224149 FOREIGN KEY (gaugeID) REFERENCES Gauge (dimension);
ALTER TABLE Wagon ADD CONSTRAINT FKWagon464700 FOREIGN KEY (wagonModelID) REFERENCES WagonModel (wagonModelID);
ALTER TABLE WagonModel ADD CONSTRAINT FKWagonModel935969 FOREIGN KEY (wagonMakerID) REFERENCES WagonMaker (wagonMakerID);
ALTER TABLE Gauge_Vehicle ADD CONSTRAINT FKGauge_Vehi602581 FOREIGN KEY (dimension) REFERENCES Gauge (dimension);
ALTER TABLE Gauge_Vehicle ADD CONSTRAINT FKGauge_Vehi503914 FOREIGN KEY (vehicleID) REFERENCES Vehicle (vehicleID);
ALTER TABLE Train_Locomotive ADD CONSTRAINT FKTrain_Loco809453 FOREIGN KEY (trainID) REFERENCES Train (trainID);
ALTER TABLE Train_Locomotive ADD CONSTRAINT FKTrain_Loco907096 FOREIGN KEY (locomotiveID) REFERENCES Locomotive (locomotiveID);
ALTER TABLE Path_Route ADD CONSTRAINT FKPath_Route679989 FOREIGN KEY (pathID) REFERENCES Path (pathID);
ALTER TABLE Path_Route ADD CONSTRAINT FKPath_Route405121 FOREIGN KEY (routeID) REFERENCES Route (routeID);
ALTER TABLE Path_Facility ADD CONSTRAINT FKPath_Facil777926 FOREIGN KEY (pathID) REFERENCES Path (pathID);
ALTER TABLE Path_Facility ADD CONSTRAINT FKPath_Facil344552 FOREIGN KEY (facilityID) REFERENCES Facility (facilityID);
ALTER TABLE Route ADD CONSTRAINT FKRoute415839 FOREIGN KEY (endPointAID) REFERENCES Facility (facilityID);
ALTER TABLE Route ADD CONSTRAINT FKRoute416800 FOREIGN KEY (endPointBID) REFERENCES Facility (facilityID);
ALTER TABLE Freight_Wagon ADD CONSTRAINT FKFreight_Wa590683 FOREIGN KEY (freightID) REFERENCES Freight (freightID);
ALTER TABLE Freight_Wagon ADD CONSTRAINT FKFreight_Wa592173 FOREIGN KEY (wagonID) REFERENCES Wagon (wagonID);
ALTER TABLE SimpleTrack ADD CONSTRAINT FKSimpleTrac818023 FOREIGN KEY (segmentID) REFERENCES LineSegment (segmentID);
ALTER TABLE DoubleTrack ADD CONSTRAINT FKDoubleTrac928673 FOREIGN KEY (segmentID) REFERENCES LineSegment (segmentID);
ALTER TABLE Siding ADD CONSTRAINT FKSiding545011 FOREIGN KEY (segmentID) REFERENCES SimpleTrack (segmentID);
ALTER TABLE Locomotive ADD CONSTRAINT FKLocomotive210019 FOREIGN KEY (startFacilityID) REFERENCES Facility (facilityID);
ALTER TABLE Train_Freight ADD CONSTRAINT FKTrain_Frei155254 FOREIGN KEY (trainID) REFERENCES Train (trainID);
ALTER TABLE Train_Freight ADD CONSTRAINT FKTrain_Frei259941 FOREIGN KEY (freightID) REFERENCES Freight (freightID);
ALTER TABLE Building ADD CONSTRAINT FKBuilding154212 FOREIGN KEY (facilityID) REFERENCES Facility (facilityID);
ALTER TABLE Train ADD CONSTRAINT FKTrain453690 FOREIGN KEY (routeID) REFERENCES Route (routeID);
ALTER TABLE Wagon ADD CONSTRAINT FKWagon32216 FOREIGN KEY (startFacility) REFERENCES Facility (facilityID);
ALTER TABLE Freight ADD CONSTRAINT FKFreight451797 FOREIGN KEY (startFacilityID) REFERENCES Facility (facilityID);
ALTER TABLE Freight ADD CONSTRAINT FKFreight582740 FOREIGN KEY (endFacilityID) REFERENCES Facility (facilityID);
ALTER TABLE Building ADD CONSTRAINT FKBuilding931962 FOREIGN KEY (buildingTypeID) REFERENCES BuildingType (buildingTypeID);
ALTER TABLE Facility ADD CONSTRAINT FKFacility471034 FOREIGN KEY (facilityTypeID) REFERENCES FacilityType (facilityTypeID);
