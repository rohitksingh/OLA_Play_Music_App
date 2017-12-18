package rohksin.com.olaplay.Callbacks;

import rohksin.com.olaplay.POJO.Music;

/**
 * Created by Illuminati on 12/16/2017.
 */

public interface AdapterItemListener {

    public void itemTouch(int  index);

    public void download(int index);

    public void firebaseAuthentication();

    public void firebaseSignOut();

}
