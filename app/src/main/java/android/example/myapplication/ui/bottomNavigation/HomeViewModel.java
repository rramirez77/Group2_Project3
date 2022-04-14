package android.example.myapplication.ui.bottomNavigation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("home");
    }
/*

    private startRun() {
        launchPrompt.isHidden = true;
        dataView.isHidden = false;
        startButton.isHidden = true;
        stopButton.isHidden = false;
    }
    private stopRun() {
      launchPrompt.isHidden = false;
      dataView.isHidden = true;
      startButton.isHidden = false;
      stopButton.isHidden = true;
    }



 */

    public LiveData<String> getText(){return mText;}

}
