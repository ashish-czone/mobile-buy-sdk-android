package com.shopify.sample.customer;

import android.support.annotation.NonNull;
import android.util.Log;

import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.shopify.sample.SampleApplication;
import com.shopify.sample.util.SharedPreferenceManager;

public class Authenticate {

    public static final String TAG = Authenticate.class.getSimpleName();

    public static void login(String email, String password) {
        Storefront.CustomerAccessTokenCreateInput input = new Storefront.CustomerAccessTokenCreateInput(email, password);
        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .customerAccessTokenCreate(input, query -> query
                        .customerAccessToken(customerAccessToken -> customerAccessToken
                                .accessToken()
                                .expiresAt()
                        )
                        .userErrors(userError -> userError
                                .field()
                                .message()
                        )
                )
        );
        SampleApplication.graphClient().mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (!response.hasErrors()) {
                    String token = response.data().getCustomerAccessTokenCreate().getCustomerAccessToken().getAccessToken();
                    Log.d(TAG, "Customer Access Token: " + token);
                    SharedPreferenceManager.getInstance().setValue("ACCESS_TOKEN", token);
                } else {
                    Log.d(TAG, response.formatErrorMessage());
                }
            }

            @Override
            public void onFailure(@NonNull GraphError error) {
                Log.e(TAG, "Could not get the access token.", error);
            }
        });
    }
}
