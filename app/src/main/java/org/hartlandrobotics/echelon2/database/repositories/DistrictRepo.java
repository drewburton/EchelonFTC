package org.hartlandrobotics.echelon2.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.hartlandrobotics.echelon2.database.EchelonDatabase;
import org.hartlandrobotics.echelon2.database.dao.RgnDao;
import org.hartlandrobotics.echelon2.database.entities.Rgn;

import java.util.List;

public class DistrictRepo {
    private RgnDao districtDao;

    public DistrictRepo(Application application){
        EchelonDatabase db = EchelonDatabase.getDatabase(application);
        districtDao = db.regionDao();

    }

    public LiveData<List<Rgn>> getDistricts(){return districtDao.getRgns();}
    // public LiveData<List<District>> getDistrictsByYear(int year){return districtDao.getDistrictsByYear(year);}

    public void upsert(Rgn district){
        EchelonDatabase.databaseWriteExecutor.execute( () -> {
            districtDao.upsert(district);
        });
    }

    public void upsert(List<Rgn> districts){
        for( Rgn d : districts){
            upsert(d);
        }
    }
}
