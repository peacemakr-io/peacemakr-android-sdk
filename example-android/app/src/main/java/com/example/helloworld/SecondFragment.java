package com.example.helloworld;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import io.peacemakr.crypto.Factory;
import io.peacemakr.crypto.ICrypto;
import io.peacemakr.crypto.exception.PeacemakrException;
import io.peacemakr.crypto.impl.persister.InMemoryPersister;

public class SecondFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    static ICrypto CryptoI = null;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        // The main thread is not usually permitted to make
        // network calls, but for the sake of a quick example,
        // we're removing that restriction.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String apiKey = "PUT YOUR API KEY HERE :)";
        InMemoryPersister persister = new InMemoryPersister();

        if (CryptoI == null) {
            try {
                CryptoI = Factory.getCryptoSDK(apiKey, "simple encrypt decrypt", null, persister, null);
            } catch (PeacemakrException e) {
                Log.e("peacemakr", "Failed to construct crypt sdk due to ", e);
                return;
            }
            try {
                CryptoI.register();
            } catch (PeacemakrException e) {
                Log.e("peacemakr", "Failed to register crypt sdk due to ", e);
                return;
            }
        }


        String plaintext = "Hello world!";
        byte[] encrypted = new byte[0];
        try {
            encrypted = CryptoI.encrypt(plaintext.getBytes());
        } catch (PeacemakrException e) {
            Log.e("peacemkar", "Failed to encrypt due to ", e);
            return;
        }
        TextView myAwesomeTextView = view.findViewById(R.id.textView);
        myAwesomeTextView.setText("Encrypted: " + new String(encrypted));
        System.out.println("Encrypted: " + new String(encrypted));
        System.out.println();

        byte[] decrypted = new byte[0];
        try {
            decrypted = CryptoI.decrypt(encrypted);
        } catch (PeacemakrException e) {
            Log.e("peacemakr", "Failed to decrypt due to ", e);
        }
        myAwesomeTextView = view.findViewById(R.id.textView2);
        myAwesomeTextView.setText("Decrypted: " + new String(decrypted));
        System.out.println("Decrypted: " + new String(decrypted));


        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }
}