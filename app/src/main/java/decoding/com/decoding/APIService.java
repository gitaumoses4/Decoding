package decoding.com.decoding;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Moses Gitau on 8/29/17 at 10:05 AM.
 */

public interface APIService {

    @POST("mahali")
    Call sendLocation(@Body Mahali mahali);
}
