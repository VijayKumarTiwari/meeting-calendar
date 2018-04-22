package org.vijayt.meetingcalendar.cucumber.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.vijayt.meetingcalendar.MeetingCalendarApplication;
import org.vijayt.meetingcalendar.controller.MeetingRequestFileInputRunner;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


@ContextConfiguration(
        classes = MeetingCalendarApplication.class,
        loader = SpringBootContextLoader.class
)
@NoArgsConstructor
public class MeetingFileProcessStepdefs {
    @Autowired
    private MeetingRequestFileInputRunner meetingRequestFileInputRunner;
    private Exception errorReported;

    @Before
    public void setUp() {
        errorReported = null;
    }

    @Given("^a file \"([^\"]*)\"$")
    public void aFile(String filePath) throws Throwable {
        meetingRequestFileInputRunner.setInputFilePath(filePath);
    }

    @When("^file is processed$")
    public void fileIsProcessed() throws Throwable {
        try {
            meetingRequestFileInputRunner.run();
        } catch (Exception e) {
            errorReported = e;
        }
    }

    @Then("^error is reported$")
    public void errorIsReported() throws Throwable {
        assertThat(errorReported, notNullValue());
    }

    @Then("^output is same as \"([^\"]*)\"$")
    public void outputIs(String outputFile) throws Throwable {
        StringBuilder output = new StringBuilder();
        Files.lines(Paths.get(outputFile)).forEach(line -> {
            output.append(line);
            output.append(System.lineSeparator());
        });
        assertThat(meetingRequestFileInputRunner.getOutput(), is(output.toString()));
    }
}
