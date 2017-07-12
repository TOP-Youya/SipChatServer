package sipchat.manager;

import javax.sip.message.Request;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by leeshun on 2017/7/12.
 */
public class RequestTaskHolder {
    private BlockingQueue<Request> requests;

    private static RequestTaskHolder holder;

    private RequestTaskHolder() {
        requests = new LinkedBlockingQueue<>();
    }

    public static RequestTaskHolder getInstance() {
        if(holder == null) {
            holder = new RequestTaskHolder();
        }
        return holder;
    }

    public void addRequest(Request request) throws InterruptedException {
        requests.put(request);
    }

    public Request getRequest() throws InterruptedException {
        return requests.take();
    }
}
