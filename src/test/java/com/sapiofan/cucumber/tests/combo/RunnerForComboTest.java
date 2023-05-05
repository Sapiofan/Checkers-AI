package com.sapiofan.cucumber.tests.combo;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"classpath:features/computerBeatsManyCheckers.feature"},
        glue = {"com.sapiofan.cucumber.tests.combo"})
public class RunnerForComboTest {
}
