package com.sapiofan.cucumber.tests.start;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"classpath:features/startGame.feature"},
        glue = {"com.sapiofan.cucumber.tests.start"})
public class RunnerTest {
}
