import com.usi.ch.syn.cli.SynCmd;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

public class TestCMD {

    @Test
    public void testListCmd(){
        new CommandLine(new SynCmd()).execute("list");
    }

    @Test
    public void addProjectCmd(){
        new CommandLine(new SynCmd()).execute("project", "-n JetUML", "-r https://github.com/prmr/JetUML.git");
    }

    @Test
    public void listFileHistory(){
        new CommandLine(new SynCmd()).execute("history", "--git", "1", "5");
    }

    @Test
    public void testAutoAnalyze(){
        new CommandLine(new SynCmd()).execute("analyze" ,"auto", "-p", "16", "-t", "1");
    }

}
