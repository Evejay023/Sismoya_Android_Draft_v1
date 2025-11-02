package com.example.waterrefilldraftv1.Riders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.waterrefilldraftv1.Riders.models.Rider;

public class RiderViewModel extends ViewModel {

    private final MutableLiveData<Rider> riderLiveData = new MutableLiveData<>();

    public void setRider(Rider rider) {
        riderLiveData.setValue(rider);
    }

    public LiveData<Rider> getRider() {
        return riderLiveData;
    }

    public void updateRiderName(String first, String last) {
        Rider rider = riderLiveData.getValue();
        if (rider != null) {
            rider.setFirstName(first);
            rider.setLastName(last);
            riderLiveData.setValue(rider);
        }
    }
}
