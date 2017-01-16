package com.artyom.androidwearpoc.wear.data;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.common.io.ByteStreams;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.artyom.androidwearpoc.MyMobileApplication;
import com.artyom.androidwearpoc.dagger.components.DaggerDBReposComponent;
import com.artyom.androidwearpoc.dagger.components.DaggerGoogleComponent;
import com.artyom.androidwearpoc.dagger.components.MobileApplicationComponent;
import com.artyom.androidwearpoc.dagger.modules.GoogleApiModule;
import com.artyom.androidwearpoc.db.AccelerometerSamplesRepo;
import com.artyom.androidwearpoc.db.BatteryLevelSamplesRepo;
import com.artyom.androidwearpoc.model.AccelerometerSampleTEMPORAL;
import com.artyom.androidwearpoc.model.BatteryLevelSample;
import com.artyom.androidwearpoc.model.MessageData;
import com.artyom.androidwearpoc.model.converter.AccelerometerSamplesConverter;
import com.artyom.androidwearpoc.report.MyLogger;
import com.artyom.androidwearpoc.report.ReportController;
import com.artyom.androidwearpoc.report.log.DataMismatchEvent;
import com.artyom.androidwearpoc.shared.DefaultConfiguration;
import com.artyom.androidwearpoc.shared.models.AccelerometerSampleData;
import com.artyom.androidwearpoc.shared.models.MessagePackage;
import com.artyom.androidwearpoc.shared.utils.ParcelableUtil;
import com.artyom.androidwearpoc.util.ConfigController;
import com.artyom.androidwearpoc.util.SharedPrefsController;
import com.bytesizebit.androidutils.DateUtils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import timber.log.Timber;

import static com.artyom.androidwearpoc.shared.CommonConstants.SENSORS_MESSAGE;
import static com.artyom.androidwearpoc.shared.DefaultConfiguration.MAX_ALLOWED_DIFF_BETWEEN_PACKAGES_IN_MILLIS;
import static com.artyom.androidwearpoc.shared.DefaultConfiguration.DEFAULT_SAMPLES_PER_PACKAGE_LIMIT;
import static com.artyom.androidwearpoc.shared.enums.DataTransferType.ASSET;

/**
 * Created by Artyom on 27/12/2016.
 */

public class DataReceiverService extends WearableListenerService
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long CLIENT_CONNECTION_TIMEOUT = 15;

    private GoogleApiClient mGoogleApiClient;

    private String mLocalNodeId;

    private SharedPrefsController mSharedPrefsController;

    private ReportController mReportController;

    private ConfigController mConfigController;

    private MyLogger mMyLogger;

    @Inject
    AccelerometerSamplesRepo mAccelerometerSamplesRepo;

    @Inject
    BatteryLevelSamplesRepo mBatteryLevelSamplesRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerDBReposComponent.builder()
                .build()
                .inject(this);

        MobileApplicationComponent applicationComponent = MyMobileApplication
                .getApplicationComponent();

        mSharedPrefsController = applicationComponent.getSharedPrefsController();
        mReportController = applicationComponent.getReportController();
        mMyLogger = applicationComponent.getMyLogger();
        mGoogleApiClient = DaggerGoogleComponent
                .builder()
                .googleApiModule(new GoogleApiModule(this.getApplicationContext(), this, this))
                .build()
                .googleApiClient();
        mConfigController = applicationComponent.getConfigController();
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Timber.d("events arrived");

        List<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);

        if (!validateGoogleClientConnection()) {
            Timber.w("failed to connect Google client");
            return;
        }

        // Running through all the events
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED && isFromOtherNode(event)) {
                processData(event);
                deleteProcessedItemFromDataLayer(event);
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Timber.d("data event deleted, uri: %s", event.getDataItem().getUri());
            }
        }
    }

    private void deleteProcessedItemFromDataLayer(DataEvent event) {
        Uri dataItemUri = event.getDataItem().getUri();
        Timber.d("deleting data item, uri: %s", dataItemUri);
        Wearable.DataApi.deleteDataItems(mGoogleApiClient, dataItemUri);
    }

    private boolean isFromOtherNode(DataEvent event) {

        // Checking if it's the same node that has fired the event
        String node = event.getDataItem().getUri().getHost();

        if (node.equals(mLocalNodeId)) {
            Timber.d("Skipping Event - same receiver");
            return false;
        } else {
            return true;
        }
    }

    private void processData(DataEvent event) {
        Timber.d("New package arrived from wearable, processing data...");

        MessagePackage messagePackage;

        if (DefaultConfiguration.DATA_TRANSFER_TYPE == ASSET) {
            messagePackage = getMessageFromAsset(event);
        } else {
            messagePackage = getMessageFromDefaultEvent(event);
        }

        if (messagePackage != null) {

            mMyLogger.logChunkToFile(messagePackage);

            Timber.d("received package, package index: %s, package amount: %s", messagePackage
                    .getIndex(), messagePackage.getAccelerometerSamples().size());

            MessageData lastSavedMessageData = mSharedPrefsController.getLastMessage();

            if (lastSavedMessageData != null) {
                verifyValues(lastSavedMessageData, messagePackage);
            }

            int messageIndex = messagePackage.getIndex();

            //TODO: revert back to - List<AccelerometerSample> converted =
            // AccelerometerSamplesConverter.convert(messagePackage.getAccelerometerSamples());
            List<AccelerometerSampleTEMPORAL> converted =
                    AccelerometerSamplesConverter.convert(messagePackage.getAccelerometerSamples(), messageIndex);
            mAccelerometerSamplesRepo.saveSamples(converted);

            float batteryPercentage = messagePackage.getBatteryPercentage();
            mBatteryLevelSamplesRepo.saveSample(new BatteryLevelSample(batteryPercentage, new Date()));

            mSharedPrefsController.saveMessage(messagePackage);

            Timber.d("Wearable message arrived, message index: %s battery level: %s", messageIndex,
                    batteryPercentage);
        } else {
            Timber.d("Wearable message values are null");
        }
    }

    private MessagePackage getMessageFromAsset(DataEvent event) {

        DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
        Asset messageAsset = dataMapItem.getDataMap().getAsset(SENSORS_MESSAGE);

        InputStream messageAssetInputStream = Wearable.DataApi
                .getFdForAsset(mGoogleApiClient, messageAsset).await().getInputStream();

        try {
            byte[] messageBytes = ByteStreams.toByteArray(messageAssetInputStream);
            return ParcelableUtil.unmarshall(messageBytes, MessagePackage.CREATOR);
        } catch (Exception e) {
            Timber.e("failed to convert input stream to byte array, reason: %s", e);
            return null;
        }
    }

    private MessagePackage getMessageFromDefaultEvent(DataEvent event) {

        byte[] data = DataMapItem
                .fromDataItem(event.getDataItem())
                .getDataMap()
                .getByteArray(SENSORS_MESSAGE);

        return ParcelableUtil.unmarshall(data, MessagePackage.CREATOR);
    }

    private void verifyValues(@NonNull MessageData lastSavedMessageData,
                              @NonNull MessagePackage newMessagePackage) {

        // Retrieving important data from new arrived package
        int newPackageSize = newMessagePackage.getAccelerometerSamples().size();
        AccelerometerSampleData firstSampleInNewPackage = newMessagePackage.getAccelerometerSamples().get(0);

        // Creating custom crashlytics event to be sent if any mismatch prsents in new arrived
        // data from wearable
        DataMismatchEvent dataMismatchEvent = new DataMismatchEvent();
        boolean isDataValid = true;

        if (newPackageSize != mConfigController.getSamplesPerChunk()) {
            dataMismatchEvent.updateSamplesAmountMismatch(mConfigController.getSamplesPerChunk(), newPackageSize);
            isDataValid = false;
        }

        long firstSampleTime = firstSampleInNewPackage.getTimestamp();
        long lastSampleTime = lastSavedMessageData.getLastSampleTimestamp();

        if (!packagesTimeDifferenceValid(lastSampleTime, firstSampleTime)) {
            dataMismatchEvent.updatePackagesTimesMismatch(lastSampleTime, firstSampleTime);
            isDataValid = false;
        }

        if (!isDataValid) {
            mReportController.sendCustomEvent(dataMismatchEvent);
        }

    }

    private boolean packagesTimeDifferenceValid(long lastSampleOldMessageTimestamp, long firstSampleNewMessageTimestamp) {
        return firstSampleNewMessageTimestamp - lastSampleOldMessageTimestamp <= MAX_ALLOWED_DIFF_BETWEEN_PACKAGES_IN_MILLIS
                && firstSampleNewMessageTimestamp - lastSampleOldMessageTimestamp > 0;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.e("google client connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("google client connection failed, connection result: %s", connectionResult.getErrorMessage());
    }

    private boolean validateGoogleClientConnection() {

        if (mGoogleApiClient.isConnected()) {
            return true;
        }

        ConnectionResult result = mGoogleApiClient
                .blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.SECONDS);

        // If we have successfully connected Google Api client we can retrieve local node ID
        // and store it once. On every event we need to check if it wasn't fired from this local
        // node
        if (result.isSuccess()) {
            mLocalNodeId = Wearable.NodeApi.getLocalNode(mGoogleApiClient).await().getNode().getId();
        }

        return result.isSuccess();
    }

}
