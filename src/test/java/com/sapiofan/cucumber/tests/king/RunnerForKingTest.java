package com.sapiofan.cucumber.tests.king;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"classpath:features/getKingAndBeatChecker.feature"},
        glue = {"com.sapiofan.cucumber.tests.king"})
public class RunnerForKingTest {
}
