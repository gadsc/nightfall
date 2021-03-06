package com.elo7.nightfall.di.providers;

import com.elo7.nightfall.di.NightfallConfigurations;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQueryListener;

import java.util.Set;

class SparkSessionProvider implements Provider<SparkSession> {

	private final NightfallConfigurations configurations;
	private final Set<StreamingQueryListener> reporterListeners;

	@Inject
	SparkSessionProvider(NightfallConfigurations configurations, Set<StreamingQueryListener> reporterListeners) {
		this.configurations = configurations;
		this.reporterListeners = reporterListeners;
	}

	@Override
	public SparkSession get() {
		SparkSession.Builder builder = SparkSession.builder();

		configurations
				.getPropertiesWithPrefix("spark.")
				.forEach(builder::config);

		boolean enableHive = configurations.getProperty("nightfall.spark.hive.enable").map(BooleanUtils::toBoolean).orElse(false);

		if (enableHive) {
			builder.enableHiveSupport();
		}

		SparkSession session = builder.getOrCreate();
		reporterListeners.forEach(session.streams()::addListener);

		return session;
	}
}
