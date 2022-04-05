package service.medical;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;

public class MedicalServiceImplTests {

    MedicalServiceImpl sut;

    @BeforeEach
    public void initEach(){
        System.out.println("Тест для метода класса MedicalServiceImpl запущен");
    }

    @AfterEach
    public void finishEach() {
        System.out.println("Тест для метода класса MedicalServiceImpl завершен");
    }

    @ParameterizedTest
    @MethodSource("sourceCheckBloodPressure")
    void testCheckBloodPressure(PatientInfo info, BloodPressure currentPressure, String expected) {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(Mockito.anyString()))
                .thenReturn(info);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);
        ArgumentCaptor<String> argumentCaptorAlert = ArgumentCaptor.forClass(String.class);

        sut = new MedicalServiceImpl(patientInfoRepository, alertService);
        sut.checkBloodPressure(Mockito.anyString(), currentPressure);

        Mockito.verify(alertService).send(argumentCaptorAlert.capture());

        Assertions.assertEquals(expected, argumentCaptorAlert.getValue());
    }

    private static Stream<Arguments> sourceCheckBloodPressure() {
        PatientInfo patientInfo;
        return Stream.of(Arguments.of(patientInfo = new PatientInfo("Семен", "Михайлов",
                                LocalDate.of(1982, 1, 16),
                                new HealthInfo(new BigDecimal("36.6"),
                                        new BloodPressure(125, 78))),
                new BloodPressure(120,78),
                String.format("Warning, patient with id: %s, need help", patientInfo.getId())),

                Arguments.of(patientInfo = new PatientInfo("123", "Иван", "Петров",
                                LocalDate.of(1980, 11, 26),
                                new HealthInfo(new BigDecimal("36.65"),
                                        new BloodPressure(120, 80))),
                        new BloodPressure(100,80),
                        String.format("Warning, patient with id: %s, need help", patientInfo.getId())));
    }

    @ParameterizedTest
    @MethodSource("sourceCheckTemperature")
    void testCheckTemperature(PatientInfo info, BigDecimal currentTemperature, String expected) {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(Mockito.anyString()))
                .thenReturn(info);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);
        ArgumentCaptor<String> argumentCaptorAlert = ArgumentCaptor.forClass(String.class);

        sut = new MedicalServiceImpl(patientInfoRepository, alertService);
        sut.checkTemperature(Mockito.anyString(), currentTemperature);

        Mockito.verify(alertService).send(argumentCaptorAlert.capture());

        Assertions.assertEquals(expected, argumentCaptorAlert.getValue());
    }

    private static Stream<Arguments> sourceCheckTemperature() {
        PatientInfo patientInfo;
        return Stream.of(Arguments.of(patientInfo = new PatientInfo("Семен", "Михайлов",
                                LocalDate.of(1982, 1, 16),
                                new HealthInfo(new BigDecimal("36.6"),
                                        new BloodPressure(125, 78))),
                        new BigDecimal("30.9"),
                        String.format("Warning, patient with id: %s, need help", patientInfo.getId())),

                Arguments.of(patientInfo = new PatientInfo("123", "Иван", "Петров",
                                LocalDate.of(1980, 11, 26),
                                new HealthInfo(new BigDecimal("36.65"),
                                        new BloodPressure(120, 80))),
                        new BigDecimal("35.1"),
                        String.format("Warning, patient with id: %s, need help", patientInfo.getId())));
    }

    @ParameterizedTest
    @MethodSource("sourceNoMessages")
    void testCheckNormalTempAndPressure(PatientInfo info, BigDecimal currentTemperature, BloodPressure currentPressure) {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById(Mockito.anyString()))
                .thenReturn(info);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);
        doNothing().when(alertService).send(Mockito.anyString());

        sut = new MedicalServiceImpl(patientInfoRepository, alertService);
        sut.checkTemperature(Mockito.anyString(), currentTemperature);
        sut.checkBloodPressure(Mockito.anyString(), currentPressure);

        Mockito.verify(alertService, Mockito.never()).send(Mockito.anyString());
    }

    private static Stream<Arguments> sourceNoMessages() {
        return Stream.of(Arguments.of(new PatientInfo("Семен", "Михайлов",
                                LocalDate.of(1982, 1, 16),
                                new HealthInfo(new BigDecimal("36.6"),
                                        new BloodPressure(125, 78))),
                        new BigDecimal("36.6"),
                        new BloodPressure(125,78)),

                Arguments.of(new PatientInfo("123", "Иван", "Петров",
                                LocalDate.of(1980, 11, 26),
                                new HealthInfo(new BigDecimal("36.65"),
                                        new BloodPressure(120, 80))),
                        new BigDecimal("36.65"),
                        new BloodPressure(120,80)));
    }

}
