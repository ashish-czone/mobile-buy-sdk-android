package com.shopify.sample.customer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.shopify.buy3.GraphCall;
import com.shopify.buy3.GraphError;
import com.shopify.buy3.GraphResponse;
import com.shopify.buy3.Storefront;
import com.shopify.graphql.support.ID;
import com.shopify.sample.SampleApplication;
import com.shopify.sample.util.SharedPreferenceManager;
import com.shopify.sample.util.Util;
import com.shopify.sample.view.cart.CheckOutWebView;

public class CustomerUtils {

    private static final String TAG = CustomerUtils.class.getSimpleName();

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
                if (!response.hasErrors() && response.data() != null) {
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

    public static void associate(ID checkoutId, String customerAccessToken, final Context context) {
        Storefront.MutationQuery mutationQuery = Storefront.mutation(mutation -> mutation
                .checkoutCustomerAssociate(checkoutId, customerAccessToken, query -> query
                        .checkout(Storefront.CheckoutQuery::webUrl)
                        .userErrors(userError -> userError
                                .field()
                                .message()
                        )
                )
        );
        SampleApplication.graphClient().mutateGraph(mutationQuery).enqueue(new GraphCall.Callback<Storefront.Mutation>() {
            @Override
            public void onResponse(@NonNull GraphResponse<Storefront.Mutation> response) {
                if (!response.hasErrors() && response.data() != null) {
                    Storefront.Checkout checkout = response.data().getCheckoutCustomerAssociate().getCheckout();
                    Log.d(TAG, "Web checkout Url: " + checkout.getWebUrl());
                    Intent intent = new Intent(context, CheckOutWebView.class);
                    intent.putExtra("URL",  checkout.getWebUrl());
                    intent.putExtra("ACCESS_TOKEN", customerAccessToken);
                    context.startActivity(intent);
                    // getCustomerDetails(context, checkout.getWebUrl(), customerAccessToken);
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

    public static void getCustomerDetails(Context context, String webUrl, String customerAccessToken) {
        Storefront.QueryRootQuery query = Storefront.query(root -> root
                .customer(customerAccessToken, customer -> customer
                        .defaultAddress(connection -> connection
                                .firstName()
                                .lastName()
                                .phone()
                                .address1()
                                .address2()
                                .city()
                                .province()
                                .country()
                                .zip()
                        )
                )
        );

        SampleApplication.graphClient().queryGraph(query).enqueue(new GraphCall.Callback<Storefront.QueryRoot>() {
            @Override public void onResponse(@NonNull GraphResponse<Storefront.QueryRoot> response) {
                if (!response.hasErrors() && response.data() != null) {
                    Storefront.MailingAddress address = response.data().getCustomer().getDefaultAddress();
                    Log.d(TAG, "Addresses: " + address.toString());
                    String url = Util.getQueryUrl(webUrl, address);
                    Log.d(TAG, "getCustomerDetails: Web checkout Url: " + url);
                    Intent intent = new Intent(context, CheckOutWebView.class);
                    intent.putExtra("URL", url);
                    intent.putExtra("ACCESS_TOKEN", customerAccessToken);
                    context.startActivity(intent);
                } else {
                    Log.d(TAG, response.formatErrorMessage());
                }
            }

            @Override public void onFailure(@NonNull GraphError error) {
                Log.e(TAG, "Failed to execute query", error);
            }
        });
    }
}
