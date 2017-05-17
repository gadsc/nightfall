package com.elo7.nightfall.di.providers;

import com.elo7.nightfall.di.NightfallConfigurations;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryListener;

class SparkSessionProvider implements Provider<SparkSession> {

	private final NightfallConfigurations configurations;
	private final StreamingQueryListener reporterListener;

	@Inject
	SparkSessionProvider(NightfallConfigurations configurations, StreamingQueryListener reporterListener) {
		this.configurations = configurations;
		this.reporterListener = reporterListener;
	}

	@Override
	public SparkSession get() {
		SparkSession.Builder builder = SparkSession.builder();

		configurations
				.getPropertiesWithPrefix("spark.")
				.entrySet()
				.forEach(entry -> builder.config(entry.getKey(), entry.getValue()));

		SparkSession session = builder.getOrCreate();
		session.streams().addListener(reporterListener);

		return session;
	}
}
