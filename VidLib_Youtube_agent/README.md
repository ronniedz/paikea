# Build and install the Rest Agent

	`mvn clean package install`

	
## Usage:

	- Add to pom where you intend to use the agent:

		<dependency>
			<groupId>cab.bean.srvcs.tube4kids</groupId>
			<artifactId>ytRestAgent</artifactId>
			<version>0.8</version>
		</dependency>

### Create an agent.

	- eg Camel:

			<bean id="youTubeAgentImpl" class="cab.bean.srvcs.tube4kids.resources.YouTubeAgentImpl">
				<constructor-arg name="apiKey" value="${youtube.apiKey}" />
			</bean>

	- eg Java:

	```
			String apiKey = prop.get("youtube.apiKey");
	 		YouTubeAgent youTubeAgent = new YouTubeAgentImpl(apiKey)
			String responseBody = youTubeAgent.runSearch(queryParameterMap)
	```

