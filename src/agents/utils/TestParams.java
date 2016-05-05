package agents.utils;

import java.io.Serializable;

/**
 * Created by pawel.bielicki on 2016-05-05.
 */
public class TestParams implements Serializable {
    public int P, K, N;

    public TestParams(int p, int k, int n) {
        P = p;
        K = k;
        N = n;
    }
}
