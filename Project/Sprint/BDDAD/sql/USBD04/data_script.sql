-- Insert Gauges
INSERT INTO Gauge (dimension, name) VALUES (1668, 'Iberian gauge');
INSERT INTO Gauge (dimension, name) VALUES (1435, 'Standard gauge');

-- Insert Line Owner
INSERT INTO LineOwner (lineOwnerID, vatNumber, name)
VALUES (1, 'PT503933813', 'Infraestruturas de Portugal, SA');

-- Insert Operator
INSERT INTO Operator (operatorID, vatNumber, name)
VALUES (1, 'PT509017800', 'Medway - Operador Ferroviário de Mercadorias, S.A');
INSERT INTO Operator(operatorID, vatNumber, name)
VALUES (2, 'PT507832388', 'Captrain Portugal S.A.');

-- Insert Facility Types (Station, Terminal, Freight_Yard)
INSERT INTO FacilityType (facilityTypeID, name, isIntermodal)
VALUES (1,'STATION', 'N');
INSERT INTO FacilityType (facilityTypeID, name, isIntermodal)
VALUES (2,'TERMINAL', 'Y');
INSERT INTO FacilityType (facilityTypeID, name, isIntermodal)
VALUES (3,'TERMINAL', 'N');
INSERT INTO FacilityType (facilityTypeID, name, isIntermodal)
VALUES (4,'FREIGHT_YARD', 'N');

-- Insert Facility
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (1, 'São Romão', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (2, 'Tamel', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (3, 'Senhora das Dores', 2);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (4, 'Lousado', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (5, 'Porto Campanhã', 2);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (6, 'Leandro', 3);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (7, 'Porto São Bento', 4);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (8, 'Barcelos', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (9, 'Vila Nova da Cerveira', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (10, 'Midões', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (11, 'Valença', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (12, 'Darque', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (13, 'Contumil', 2);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (14, 'Ermesinde', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (15, 'São Frutuoso', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (16, 'São Pedro da Torre', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (17,'Viana do Castelo', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (18, 'Famalicão', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (19, 'Barroselas', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (20, 'Nine', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (21, 'Caminha', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (22, 'Carvalha', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (23, 'Carreço', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (30, 'Braga', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (31, 'Mangazão', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (32, 'Cerqueiral', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (33, 'Gemieira', 1);;
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (35, 'Paredes de Coura', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (43, 'São Gemil', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (45, 'São Mamede de Infesta', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (48, 'Leça do Balio', 1);
INSERT INTO Facility (facilityID, name, facilityTypeID)
VALUES (50, 'Leixões', 2);

-- Insert Building Type
INSERT INTO BuildingType(buildingTypeID, name) VALUES (1, 'Warehouse');
INSERT INTO BuildingType(buildingTypeID, name) VALUES (2, 'Grain Silo');
INSERT INTO BuildingType(buildingTypeID, name) VALUES (3, 'Refrigerated Area');

-- Insert Buildings
INSERT INTO Building(buildingID, facilityID, buildingTypeID) VALUES(1, 50, 1);
INSERT INTO Building(buildingID, facilityID, buildingTypeID) VALUES(2, 50, 2);
INSERT INTO Building(buildingID, facilityID, buildingTypeID) VALUES(3, 50, 3);
INSERT INTO Building(buildingID, facilityID, buildingTypeID) VALUES(4, 1, 1);
INSERT INTO Building(buildingID, facilityID, buildingTypeID) VALUES(5, 1, 2);
INSERT INTO Building(buildingID, facilityID, buildingTypeID) VALUES(6, 1, 3);



-- Insert Lines
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (1, 7, 5, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (2, 5, 13, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (3, 13, 20, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (4, 20, 8, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (5, 8, 12, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (6, 12, 17, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (7, 17, 21, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (8, 21, 16, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (9, 16, 11, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (21, 13, 43, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (22, 43, 45, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (23, 45, 48, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (24, 48, 50, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (30, 31, 30, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (31, 20, 31, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (32, 31, 32, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (35, 32, 33, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (36, 33, 35, 1, 1668, 'Y');
INSERT INTO Line (lineID, startFacilityID, endFacilityID, lineOwnerID, gaugeID, isElectrified)
VALUES (37, 35, 11, 1, 1668, 'Y');

-- Insert Line Segments
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (1, 1, 1, 8000, 2618, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (3, 2, 1, 8000, 2443, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (10, 3, 1, 8000, 26560, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (11, 3, 2, 8000, 10000, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (15, 4, 1, 8000, 5286,  'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (16,  4,2,  8000, 6000, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (14, 5, 1, 8000, 10387, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (12, 5, 2, 8000, 12000, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (13, 5, 3, 8000, 3100, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (20, 6, 1, 6400, 4890, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (18, 7, 1, 8000, 6000, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (21, 7, 2, 8000, 5000, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (22, 7, 3, 8000, 12000, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (25, 8, 1, 8000, 20829, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (26, 9, 1, 8000, 4264, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (30, 21, 1, 8000, 3883, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (31, 22, 1, 8400, 1174, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (32, 22, 2, 8000, 2534, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (33, 23, 1, 8000, 1566, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (34, 23, 2, 8000, 1453, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (35, 24, 1, 8100, 3597, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (36, 24, 2, 8000, 3597, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (50, 30, 1, 8000, 3555, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (51, 31, 5, 8000, 1222, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (52, 31, 4, 8000, 1760, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (53, 31, 3, 8000, 1720, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (54, 31, 2, 8000, 3350, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (55, 31, 1, 8000, 3470, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (58, 32, 1, 8000, 8050, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (59, 35, 1, 8000, 22320, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (60, 36, 1, 8000, 16310, 'Y', 120);
INSERT INTO LineSegment (segmentID, lineID, orderLine, maxWeight, length, maintenanceStatus, speedLimit)
VALUES (61, 37, 1, 8000, 15200, 'Y', 120);

-- Insert Double Track Segments
INSERT INTO DoubleTrack(segmentID) VALUES (1);
INSERT INTO DoubleTrack(segmentID) VALUES (3);
INSERT INTO DoubleTrack(segmentID) VALUES (10);
INSERT INTO DoubleTrack(segmentID) VALUES (11);
INSERT INTO DoubleTrack(segmentID) VALUES (15);
INSERT INTO DoubleTrack(segmentID) VALUES (16);
INSERT INTO DoubleTrack(segmentID) VALUES (14);
INSERT INTO DoubleTrack(segmentID) VALUES (12);
INSERT INTO DoubleTrack(segmentID) VALUES (13);
INSERT INTO DoubleTrack(segmentID) VALUES (20);
INSERT INTO DoubleTrack(segmentID) VALUES (30);
INSERT INTO DoubleTrack(segmentID) VALUES (31);
INSERT INTO DoubleTrack(segmentID) VALUES (32);
INSERT INTO DoubleTrack(segmentID) VALUES (33);
INSERT INTO DoubleTrack(segmentID) VALUES (34);
INSERT INTO DoubleTrack(segmentID) VALUES (35);
INSERT INTO DoubleTrack(segmentID) VALUES (36);
INSERT INTO DoubleTrack(segmentID) VALUES (50);
INSERT INTO DoubleTrack(segmentID) VALUES (51);
INSERT INTO DoubleTrack(segmentID) VALUES (52);
INSERT INTO DoubleTrack(segmentID) VALUES (53);
INSERT INTO DoubleTrack(segmentID) VALUES (54);
INSERT INTO DoubleTrack(segmentID) VALUES (55);
INSERT INTO DoubleTrack(segmentID) VALUES (58);
INSERT INTO DoubleTrack(segmentID) VALUES (59);
INSERT INTO DoubleTrack(segmentID) VALUES (60);
INSERT INTO DoubleTrack(segmentID) VALUES (61);


--Insert Single Track
INSERT INTO SimpleTrack(segmentID) VALUES (18);
INSERT INTO SimpleTrack(segmentID) VALUES (21);
INSERT INTO SimpleTrack(segmentID) VALUES (22);
INSERT INTO SimpleTrack(segmentID) VALUES (25);
INSERT INTO SimpleTrack(segmentID) VALUES (26);

-- Insert Siding
INSERT INTO Siding(sidingID, segmentID, position, length) VALUES (1, 21,2000, 864 );
INSERT INTO Siding(sidingID, segmentID, position, length) VALUES (2, 25, 11000, 266);

-- Insert LocomotiveMake
INSERT INTO LocomotiveMake (makeId, name) VALUES (1,'Siemens');
INSERT INTO LocomotiveMake (makeId, name) VALUES (2,'Sorefame - Alsthom');
INSERT INTO LocomotiveMake (makeId, name) VALUES (3,'Stadler');

-- Insert WagonMaker
INSERT INTO WagonMaker (wagonMakerID, name) VALUES (1,'Metalsines');
INSERT INTO WagonMaker (wagonMakerID, name) VALUES (2, 'Equimetal');
INSERT INTO WagonMaker (wagonMakerID, name) VALUES (3, 'Sepsa Cometna');
INSERT INTO WagonMaker (wagonMakerID, name) VALUES (4, 'Emef');
INSERT INTO WagonMaker (wagonMakerID, name) VALUES (5, 'Simmering');

-- Insert Vehicle (Locomotives)
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (1, 'Eurosprinter', 87, 3, 4.375, 19.2, 2, 220);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (2, 'CP 1900', 117, 3.062, 4.31, 19.084, 2, 100);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (3, 'E4000', 124, 3000, 4.264, 23.02, 3, 120);

-- Insert Vehicle (Wagon)
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (4, 'Tadgs 32 94 082 3', 24, 3072, 4270, 17240, 2, 120);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (5, 'Tdgs 41 94 074 1', 13.8, 3120, 4165.5, 9640, 2, 100);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (6, 'Gabs 81 94 181 1', 29.8, 3180, 4170, 21700, 2, 100);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (7, 'Regmms 32 94 356 3', 21.2, 3104, 2535, 14040, 2, 120);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (8, 'Lgs 22 94 441 6', 11.9, 2850, 1060, 13860, 2, 120);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (9, 'Sgnss 12 94 455 2', 21.6, 2950, 1030, 18116, 2, 120);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (10, 'Zaes 81 94 788', 22.9, 2950, 4226, 13800, 2, 120);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (11, 'Kbs 41 94 333', 14.2, 2842, 3300, 14020, 2, 100);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (12, 'Vehicle_No_Gauge', 15.2, 2.5, 3250, 12500, 2, 100);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (100, 'LocoVehicle', 80, 3, 4, 20, 8, 200);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (200, 'WagonVehicle', 30, 3, 4, 15, 8, 120);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (101, 'LocoVehicle2', 90, 3, 4, 50, 8, 200);
INSERT INTO Vehicle (vehicleID, name, weight, width, height, length, numberOfWheels, maxSpeed)
VALUES (201, 'BigWagonVehicle', 40, 3, 4, 30, 8, 120);

-- Insert Locomotive Model
INSERT INTO LocomotiveModel (locomotiveModelID, vehicleID, power)
VALUES (1, 1, 5600);
INSERT INTO LocomotiveModel (locomotiveModelID, vehicleID, power)
VALUES (2, 2, 1623);
INSERT INTO LocomotiveModel(locomotiveModelID, vehicleID, power)
VALUES (3,3, 3178);
INSERT INTO LocomotiveModel (locomotiveModelID, vehicleID, power)
VALUES (10, 100, 4000);
INSERT INTO LocomotiveModel (locomotiveModelID, vehicleID, power)
VALUES (11, 101, 4500);

-- Insert LocomotiveModel_Electric
INSERT INTO LocomotiveModel_Electric (locomotiveModelID, frequency, voltage)
VALUES (1, 50, 25000);

-- Insert LocomotiveModel_Diesel
INSERT INTO LocomotiveModel_Diesel (locomotiveModelID, fuelCapacity)
VALUES (2, 4882);
INSERT INTO LocomotiveModel_Diesel(locomotiveModelID, fuelCapacity)
VALUES (3, 6700);

-- Insert Locomotives
INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId, startFacilityID, name, serviceYear, operationalSpeed, status)
VALUES (5621, 1, 1, 1,50 , 'Inês', 1995, 70, 'ASSIGNED');
INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId, startFacilityID, name, serviceYear, operationalSpeed, status)
VALUES (5623, 1, 1, 1, 50, 'Paz', 1995, 70, 'ASSIGNED');
INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId, startFacilityID, name, serviceYear, operationalSpeed, status)
VALUES (5630, 1, 1, 1, 50, 'Helena', 1996, 70, 'AVAILABLE');
INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId, startFacilityID, name, serviceYear, operationalSpeed, status)
VALUES (1903, 1, 2, 2, 50, 'Eva', 1981, 42.5, 'AVAILABLE');
INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId, startFacilityID, name, serviceYear, operationalSpeed, status)
VALUES (5034, 1, 3, 3, 50, 'Adriana', 2017, 100, 'AVAILABLE');
INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId, startFacilityID, name, serviceYear, operationalSpeed, status)
VALUES (5036, 1, 3, 3, 50, 'Marina', 2017, 100, 'AVAILABLE');
INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId, startFacilityID, name, serviceYear, operationalSpeed, status)
VALUES (335001, 2, 3, 3, 5, null, 2019, 100,'AVAILABLE');
INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId, startFacilityID, name, serviceYear, operationalSpeed, status)
VALUES (335003, 2, 3, 3, 5, null, 2019, 100, 'AVAILABLE');
INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId, startFacilityID, serviceYear, operationalSpeed, status)
VALUES (1, 1, 10, 1, 1, 2015, 160, 'AVAILABLE');
INSERT INTO Locomotive (locomotiveID, operatorID, locomotiveModelID, makeId, startFacilityID, serviceYear, operationalSpeed, status)
VALUES (2, 1, 11, 1, 1, 2016, 160, 'AVAILABLE');

-- Insert Gauge_Vehicle (For Locomotives)
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 1);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 2);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 3);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1435, 3);

-- Insert Wagon_Type
INSERT INTO WagonType (wagonTypeID, name) VALUES (1,'grain wagon');
INSERT INTO WagonType (wagonTypeID, name) VALUES (2,'Wood wagons');
INSERT INTO WagonType (wagonTypeID, name) VALUES (3,'Biodiesel wagon');
INSERT INTO WagonType (wagonTypeID, name) VALUES (4,'Container wagon (max 40" HC)');

-- Insert WagonModel
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID, tareTons, maxPayload, volume)
VALUES (1245, 4, 1, 1, 24, 56, 75);
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID, tareTons, maxPayload, volume)
VALUES (1278, 5, 4, 2, 13.8, 26, 38);
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID, tareTons, maxPayload, volume)
VALUES (1325, 6, 1, 3, 29.8, 50, 110);
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID, tareTons, maxPayload, volume)
VALUES (1104, 7, 1, 1, 21.2, 60, 76.3);
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID, tareTons, maxPayload, volume)
VALUES (985, 8, 3, 1, 11.9, 28, 76.3);
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID, tareTons, maxPayload, volume)
VALUES (987, 9, 1, 4, 21.6, 68, 76.3);
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID, tareTons, maxPayload, volume)
VALUES (1525, 10, 3, 1, 11.9, 28, 64.6);
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID, tareTons, maxPayload, volume)
VALUES (1523, 11, 4, 5, 14.2, 68, 64.6);
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID,tareTons, maxPayload, volume)
VALUES (90091, 12, 1, 1, 10, 20, 30);
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID, tareTons, maxPayload, volume)
VALUES (20, 200, 1, 1, 10, 60, 100);
INSERT INTO WagonModel (wagonModelID, vehicleID, wagonTypeID, wagonMakerID, tareTons, maxPayload, volume)
VALUES (21, 201, 1, 1, 12, 60, 100);

-- Insert Wagons
-- Regmms wagons (Model 1104)
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563077, 1, 1104,50 , 1987, 60.6, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563078, 1, 1104,50, 1987, 60.6, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563079, 1, 1104,50, 1987, 60.6, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563080, 1, 1104,50, 1987, 60.6, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563081, 1, 1104,50, 1987, 60.6, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563082, 1, 1104,50, 1987, 60.6, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563083, 1, 1104,50, 1987, 60.6, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563084, 1, 1104,50, 1987, 60.6, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563085, 1, 1104,50, 1987, 60.6, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563086, 1, 1104,50, 1987, 60.6,  'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563087, 1, 1104,50, 1987, 60.6, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563088, 1, 1104,50, 1987, 60.6, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563089, 1, 1104,50, 1987, 60.6, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563090, 1, 1104,50, 1987, 60.6, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563091, 1, 1104,50, 1987, 60.6, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3563092, 1, 1104,50, 1987, 60.6, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (1, 1, 20, 1, 2018, 50, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3, 1, 21, 1, 2020, 50, 'AVAILABLE');

-- Tadgs wagons (Model 1245)
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (0823045, 1, 1245,50, 1990, 56, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (0823046, 1, 1245,50, 1990, 56, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (0823047, 1, 1245,50, 1990, 56, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (0823048, 1, 1245,50, 1990, 56, 'AVAILABLE');

-- Tdgs wagons (Model 1278)
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (0741001, 1, 1278,50, 1977, 26.238, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (0741002, 1, 1278,50, 1977, 26.238, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (0741003, 1, 1278,50, 1977, 26.238, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (0741004, 1, 1278,50, 1977, 26.238, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (0741005, 1, 1278,50, 1977, 26.238, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (0741006, 1, 1278,50, 1977, 26.238, 'AVAILABLE');

-- Gabs wagons (Model 1325)
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (1811010, 1, 1325,50, 1977, 50.2, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (1811011, 1, 1325,50, 1977, 50.2, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (1811012, 1, 1325,50, 1977, 50.2, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (1811013, 1, 1325,50, 1977, 50.2, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (1811014, 1, 1325,50, 1977, 50.2, 'AVAILABLE');

-- Kbs wagons (Model 1523)
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3330001, 1, 1523,50, 2005, 68, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3330002, 1, 1523,50, 2005, 68, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3330003, 1, 1523,50, 2005, 68, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3330004, 1, 1523,50, 2005, 68, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3330005, 1, 1523,50, 2005, 68, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3330006, 1, 1523,50, 2005, 68, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3330007, 1, 1523,50, 2005, 68, 'ASSIGNED');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3330008, 1, 1523,50, 2005, 68, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3330009, 1, 1523,50, 2005, 68, 'AVAILABLE');
INSERT INTO Wagon (wagonID, operatorID, wagonModelID, startFacility, serviceYear, payloadTons, status)
VALUES (3330010, 1, 1523,50, 2005, 68, 'AVAILABLE');

-- Insert Gauge_Vehicle (for wagons)
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 4);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 5);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 6);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 7);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 8);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 9);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1435, 9);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 10);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1435, 10);
INSERT INTO Gauge_Vehicle (dimension, vehicleID) VALUES (1668, 11);

-- Insert Routes
INSERT INTO Route (routeID, endPointAID, endPointBID) VALUES (1, 50, 11); -- Route ID 1: Leixões - Valença
INSERT INTO Route (routeID, endPointAID, endPointBID) VALUES (2, 11, 50); -- Route ID 2: Valença - Leixões

-- Insert Trains
INSERT INTO Train (trainID, routeID, startDate, maxLenght, status)
VALUES (5421, 1, TO_TIMESTAMP('2025-10-03 09:45:00', 'YYYY-MM-DD HH24:MI:SS'), 1000000, 'PLANNED');
INSERT INTO Train (trainID, routeID, startDate, maxLenght, status)
VALUES (5435, 2, TO_TIMESTAMP('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1000000, 'PLANNED');
INSERT INTO Train (trainID, routeID, startDate, maxLenght, status)
VALUES (5437, 2, TO_TIMESTAMP('2025-10-06 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1000000, 'PLANNED');
INSERT INTO Train (trainID, routeID, startDate, maxLenght, status)
VALUES (1, 1, SYSTIMESTAMP, 60, 'PLANNED');

-- Insert Locomotive to train
INSERT INTO Train_Locomotive (trainID, locomotiveID) VALUES (5421, 5621);
INSERT INTO Train_Locomotive (trainID, locomotiveID) VALUES (5421, 5623);
INSERT INTO Train_Locomotive (trainID, locomotiveID) VALUES (5435, 5623);
INSERT INTO Train_Locomotive (trainID, locomotiveID) VALUES (5437, 5621);

-- Insert Freight records
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2001, 50, 12, TO_TIMESTAMP('2025-10-03 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2002, 13, 11, TO_TIMESTAMP('2025-10-03 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2003, 50, 11, TO_TIMESTAMP('2025-10-03 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2004, 50, 21, TO_TIMESTAMP('2025-10-03 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2005, 20, 11, TO_TIMESTAMP('2025-10-03 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2006, 12, 50, TO_TIMESTAMP('2025-10-03 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2007, 50, 5, TO_TIMESTAMP('2025-10-03 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2050, 12, 50, TO_TIMESTAMP('2025-10-06 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2051, 11, 50, TO_TIMESTAMP('2025-10-06 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (1, 1, 2, SYSTIMESTAMP);
INSERT INTO Freight (freightID, startFacilityID, endFacilityID, "date")
VALUES (2, 1, 2, SYSTIMESTAMP);

-- Freight wagons
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330001, 2001);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330002, 2001);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330004, 2001);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330005, 2001);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330006, 2001);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563089, 2002);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (1811011, 2003);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (1811012, 2003);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (1811013, 2004);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563077, 2005);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563078, 2005);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563079, 2005);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563080, 2005);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330007, 2006);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330003, 2006);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563090, 2007);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563091, 2007);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563092, 2007);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330001, 2050);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330002, 2050);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330004, 2050);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330005, 2050);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3330006, 2050);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (1811011, 2051);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (1811012, 2051);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563077, 2051);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563078, 2051);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563079, 2051);
INSERT INTO Freight_Wagon (wagonID, freightID) VALUES (3563080, 2051);
INSERT INTO Freight_Wagon (freightID, wagonID)
VALUES (1, 1);
INSERT INTO Freight_Wagon (freightID, wagonID)
VALUES (2, 3);

-- Insert freights into train
INSERT INTO Train_Freight (trainID, freightID) VALUES (5421, 2001);
INSERT INTO Train_Freight (trainID, freightID) VALUES (5421, 2002);
INSERT INTO Train_Freight (trainID, freightID) VALUES (5421, 2003);
INSERT INTO Train_Freight (trainID, freightID) VALUES (5421, 2004);
INSERT INTO Train_Freight (trainID, freightID) VALUES (5421, 2005);
INSERT INTO Train_Freight (trainID, freightID) VALUES (5435, 2006);
INSERT INTO Train_Freight (trainID, freightID) VALUES (5437, 2050);
INSERT INTO Train_Freight (trainID, freightID) VALUES (5437, 2051);

-- Insert Paths
INSERT INTO Path (pathID) VALUES (1);
INSERT INTO Path (pathID) VALUES (2);
INSERT INTO Path (pathID) VALUES (3);

-- Path 1: Facilities 50,48,45,43,13,20,8,12,17,21,16,11
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 50, 1, TO_TIMESTAMP('2025-10-03 09:45:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 09:45:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 48, 2, TO_TIMESTAMP('2025-10-03 09:48:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 09:48:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 45, 3, TO_TIMESTAMP('2025-10-03 09:51:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 09:51:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 43, 4, TO_TIMESTAMP('2025-10-03 09:54:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 09:54:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 13, 5, TO_TIMESTAMP('2025-10-03 09:57:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 10:00:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 20, 6, TO_TIMESTAMP('2025-10-03 10:03:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 10:06:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 8, 7, TO_TIMESTAMP('2025-10-03 10:09:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 10:09:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 12, 8, TO_TIMESTAMP('2025-10-03 10:12:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 10:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 17, 9, TO_TIMESTAMP('2025-10-03 10:18:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 10:18:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 21, 10, TO_TIMESTAMP('2025-10-03 10:21:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 10:24:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 16, 11, TO_TIMESTAMP('2025-10-03 10:27:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 10:27:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (1, 11, 12, TO_TIMESTAMP('2025-10-03 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 10:33:00', 'YYYY-MM-DD HH24:MI:SS'));

-- Path 2: Facilities 11, 16, 21, 17, 12, 8, 20, 13, 43, 45, 48, 50
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 11, 1,TO_TIMESTAMP('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:00:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 16, 2, TO_TIMESTAMP('2025-10-03 18:03:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:03:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 21, 3, TO_TIMESTAMP('2025-10-03 18:06:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:06:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 17, 4, TO_TIMESTAMP('2025-10-03 18:09:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:09:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 12, 5, TO_TIMESTAMP('2025-10-03 18:12:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 8, 6, TO_TIMESTAMP('2025-10-03 18:18:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:18:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 20, 7, TO_TIMESTAMP('2025-10-03 18:21:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:21:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 13, 8, TO_TIMESTAMP('2025-10-03 18:24:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:24:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 43, 9, TO_TIMESTAMP('2025-10-03 18:27:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:27:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 45, 10, TO_TIMESTAMP('2025-10-03 18:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 48, 11, TO_TIMESTAMP('2025-10-03 18:33:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:33:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (2, 50, 12, TO_TIMESTAMP('2025-10-03 18:36:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-03 18:36:00', 'YYYY-MM-DD HH24:MI:SS'));

-- Path 3: Facilities 11, 16, 21, 17, 12, 8, 20, 13, 43, 45, 48, 50
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 11, 1,TO_TIMESTAMP('2025-10-06 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:00:00', 'YYYY-MM-DD HH24:MI:SS') );
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 16, 2, TO_TIMESTAMP('2025-10-06 10:03:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:03:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 21, 3, TO_TIMESTAMP('2025-10-06 10:06:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:06:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 17, 4, TO_TIMESTAMP('2025-10-06 10:09:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:09:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 12, 5, TO_TIMESTAMP('2025-10-06 10:12:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:15:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 8, 6, TO_TIMESTAMP('2025-10-06 10:18:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:18:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 20, 7, TO_TIMESTAMP('2025-10-06 10:21:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:21:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 13, 8, TO_TIMESTAMP('2025-10-06 10:24:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:24:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 43, 9, TO_TIMESTAMP('2025-10-06 10:27:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:27:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 45, 10, TO_TIMESTAMP('2025-10-06 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:30:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 48, 11, TO_TIMESTAMP('2025-10-06 10:33:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:33:00', 'YYYY-MM-DD HH24:MI:SS'));
INSERT INTO Path_Facility (pathID, facilityID, "order", arrivalTime, departureTime)
VALUES (3, 50, 12, TO_TIMESTAMP('2025-10-06 10:36:00', 'YYYY-MM-DD HH24:MI:SS'), TO_TIMESTAMP('2025-10-06 10:36:00', 'YYYY-MM-DD HH24:MI:SS'));

-- Associate Paths with Routes
INSERT INTO Path_Route (pathID, routeID) VALUES (1, 1);
INSERT INTO Path_Route (pathID, routeID) VALUES (2, 2);
INSERT INTO Path_Route (pathID, routeID) VALUES (3, 2);

COMMIT;