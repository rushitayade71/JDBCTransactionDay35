import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmployeePayrollServiceTest {
    @Test
    public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1,"Rushi", 5000000.00),
                new EmployeePayrollData(2, "Prajakta", 6000000.00),
                new EmployeePayrollData(3, "Kartik", 4000000.00)
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
        assertEquals(3,entries);
    }

    @Test
    public void givenFileReadingFromFileShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        assertEquals(3, employeePayrollData.size());
    }

    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        assertEquals(3, employeePayrollData.size());

    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_shouldSyncWithDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.updateEmployeeSalary("Rushi",7000000.00);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Rushi");
        assertTrue(result);
    }

    @Test
    public  void givenDataRange_WhenRetrived_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService= new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        LocalDate startDate = LocalDate.of(2018,01,01);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayrollData> employeePayrollData =
                employeePayrollService.readEmployeePayrollForDataRange(EmployeePayrollService.IOService.DB_IO, startDate, endDate);
        assertEquals(3,employeePayrollData.size());
    }

    @Test
    public void givenPayrollData_WhenAverageSalaryRetrivedByGender_ShouldReturnProperValue() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData( EmployeePayrollService.IOService.DB_IO);
        Map<String,Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        assertTrue(averageSalaryByGender.get("M").equals(5500000.00) &&
                averageSalaryByGender.get("F").equals(6000000.00));
    }

    @Test
    void givenNewEmployee_WhenAdded_ShouldSyncWithDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.addEmployeeToPayroll("Akshay", "M", 5000000.00, LocalDate.now());
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Akshay");
        assertTrue(result);
    }
}
