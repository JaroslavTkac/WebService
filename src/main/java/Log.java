import lombok.Data;


/**
 * Created by jaroslavtkaciuk on 12/05/2017.
 */

@Data
class Log {

    private int id;
    private String timestamp;
    private String URL;
    private String method;
    private String request;
    private String headerPATH;
    private String headerMETHOD;
    private String headerERROR;
    private int responseCode;

    Log(int id, int responseCode, String timestamp, String URL, String method, String request, String headerPATH, String headerMETHOD, String headerERROR){
        this.id = id;
        this.responseCode = responseCode;
        this.timestamp = timestamp;
        this.method = method;
        this.request = request;
        this.headerPATH = headerPATH;
        this.headerMETHOD = headerMETHOD;
        this.headerERROR = headerERROR;
        this.URL = URL;
    }


}
