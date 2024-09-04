package com.makiti_group.matoa_dev.service;

import com.makiti_group.matoa_dev.model.Bus;
import com.makiti_group.matoa_dev.model.City;
import com.makiti_group.matoa_dev.model.Operator;
import com.makiti_group.matoa_dev.model.Route;
import com.makiti_group.matoa_dev.repository.BusRepository;
import com.makiti_group.matoa_dev.repository.CityRepository;
import com.makiti_group.matoa_dev.repository.OperatorRepository;
import com.makiti_group.matoa_dev.repository.RouteRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.Time;

@Service
public class ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Transactional
    public void importDataFromExcel(InputStream file) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            // Print all sheet names for debugging
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                System.out.println("Sheet name: " + workbook.getSheetName(i));
            }

            // Ensure the correct sheet is used
            Sheet sheet = workbook.getSheet("Cerises Express");

            for (Row row : sheet) {
                if (row.getRowNum() == 0 && row.getRowNum() == 1 ) { // Skip header row
                    continue;
                }

                if (isRowEmpty(row)) {
                    continue; // Skip empty row
                }

                // Adjust cell indexes according to your Excel structure
                String operatorName = getStringCellValue(row.getCell(1)); // Opérateurs
                String departureCity = getStringCellValue(row.getCell(3)); // départs
                String arrivalCity = getStringCellValue(row.getCell(5)); // arrivées
                String busType = getStringCellValue(row.getCell(6)); // Type
                String busModel0 = getStringCellValue(row.getCell(7)); // modele de bus
                String busModel = getStringCellValue(row.getCell(8)); // modele de bus
                Integer busCapacity = getIntegerCellValue(row.getCell(9)); // nombre de place

                Double fareAdultSingle = getDoubleCellValue(row.getCell(10)); // tarifs Adultes Aller Simple
                Double fareChildSingle = getDoubleCellValue(row.getCell(11)); // tarifs Enfants Aller Simple
                Double fareAdultReturn = getDoubleCellValue(row.getCell(12)); // tarifs Adultes Aller-Retour
                Double fareChildReturn = getDoubleCellValue(row.getCell(13)); // tarifs Enfants Aller-Retour
                Double schedule = getDoubleCellValue(row.getCell(14)); // horaires
                String days = getStringCellValue(row.getCell(15)); // Jours
                String vehicleNumber = getStringCellValue(row.getCell(16)); // Mat. Vehicule
                String point = getStringCellValue(row.getCell(17)); // point
                String longitude = getStringCellValue(row.getCell(18)); // longitude
                String latitude = getStringCellValue(row.getCell(19)); // latitude

                // Print retrieved values
                System.out.println("Operator Name: " + operatorName);
                System.out.println("Departure City: " + departureCity);
                System.out.println("Arrival City: " + arrivalCity);
                System.out.println("Bus Type: " + busType);
                System.out.println("Bus Model: " + busModel);
                System.out.println("Bus Capacity: " + busCapacity);
                System.out.println("Fare Adult Single: " + fareAdultSingle);
                System.out.println("Fare Child Single: " + fareChildSingle);
                System.out.println("Fare Adult Return: " + fareAdultReturn);
                System.out.println("Fare Child Return: " + fareChildReturn);
                System.out.println("Schedule: " + schedule);
                System.out.println("Days: " + days);
                System.out.println("Vehicle Number: " + vehicleNumber);
                System.out.println("Point: " + point);
                System.out.println("Longitude: " + longitude);
                System.out.println("Latitude: " + latitude);

                // Validate fields
                String validationErrors = validateFields(operatorName, departureCity, arrivalCity, busType, busModel, busCapacity, fareAdultSingle, fareChildSingle, fareAdultReturn, fareChildReturn, schedule, days, vehicleNumber);

                if (!validationErrors.isEmpty()) {
                    logger.error("Invalid data at row {}: {}", row.getRowNum(), validationErrors);
                    continue;
                }


                // Save entities
                Operator operator = operatorRepository.findByName(operatorName)
                        .orElseGet(() -> operatorRepository.save(new Operator(operatorName)));

                City departure = cityRepository.findByName(departureCity)
                        .orElseGet(() -> cityRepository.save(new City(departureCity)));

                City arrival = cityRepository.findByName(arrivalCity)
                        .orElseGet(() -> cityRepository.save(new City(arrivalCity)));

                Bus bus = busRepository.findByNumber(vehicleNumber)
                        .orElseGet(() -> busRepository.save(new Bus(busType, busModel, busCapacity, vehicleNumber)));

                Route route = new Route();
                route.setOperator(operator);
                route.setDepartureCity(departure);
                route.setArrivalCity(arrival);
                route.setBus(bus);
                route.setAdultPrice(fareAdultSingle);
                route.setChildPrice(fareChildSingle);
                route.setTripAdultPrice(fareAdultReturn);
                route.setTripChildPrice(fareChildReturn);
                route.setDepartureTime(parseNumericTime(schedule));
                route.setDay(days);
                route.setDirection("aller");

                // Additional fields (if used in your model)
                //route.setPoint(point);
                //route.setLongitude(longitude);
                //route.setLatitude(latitude);

                routeRepository.save(route);
            }
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int cellIndex = 0; cellIndex < 20; cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private String validateFields(String operatorName, String departureCity, String arrivalCity, String busType, String busModel, Integer busCapacity, Double fareAdultSingle, Double fareChildSingle, Double fareAdultReturn, Double fareChildReturn, Double schedule, String days, String vehicleNumber) {
        StringBuilder validationErrors = new StringBuilder();

        if (isNullOrEmpty(operatorName)) validationErrors.append("Operator Name is empty; ");
        if (isNullOrEmpty(departureCity)) validationErrors.append("Departure City is empty; ");
        if (isNullOrEmpty(arrivalCity)) validationErrors.append("Arrival City is empty; ");
        if (isNullOrEmpty(busType)) validationErrors.append("Bus Type is empty; ");
        if (isNullOrEmpty(busModel)) validationErrors.append("Bus Model is empty; ");
        if (busCapacity == null || busCapacity <= 0) validationErrors.append("Bus Capacity is invalid; ");
        if (fareAdultSingle == null || fareAdultSingle <= 0) validationErrors.append("Fare Adult Single is missing or invalid; ");
        if (fareChildSingle == null || fareChildSingle <= 0) validationErrors.append("Fare Child Single is missing or invalid; ");
        if (fareAdultReturn == null || fareAdultReturn <= 0) validationErrors.append("Fare Adult Return is missing or invalid; ");
        if (fareChildReturn == null || fareChildReturn <= 0) validationErrors.append("Fare Child Return is missing or invalid; ");
        if (schedule == null) validationErrors.append("Schedule is empty; ");
        if (isNullOrEmpty(days)) validationErrors.append("Days are missing; ");
        if (isNullOrEmpty(vehicleNumber)) validationErrors.append("Vehicle Number is empty; ");

        return validationErrors.toString();
    }


    private Time parseNumericTime(Double numericValue) {
        if (numericValue == null) {
            return null;
        }
        long milliseconds = (long) (numericValue * 86400000L); // Convert fraction of a day to milliseconds
        return new Time(milliseconds);
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default: return "";
        }
    }

    private Integer getIntegerCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC: return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default: return null;
        }
    }

    private Double getDoubleCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC: return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default: return null;
        }
    }
}
