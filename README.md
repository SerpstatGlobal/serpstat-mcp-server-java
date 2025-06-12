# Serpstat MCP Server

MCP \(Model Context Protocol\) server for integration with Serpstat API, implementing the `SerpstatDomainProcedure.getDomainsInfo` method.

## Requirements

- **Java**: 17+
- **Maven**: 3.6+
- **Serpstat API token**

## Installation and Launch

1. **Clone or create the project:**
   ```bash
   mkdir serpstat-mcp-server
   cd serpstat-mcp-server
   ```

2. **Set the environment variable with the API token:**
   ```bash
   export SERPSTAT_API_TOKEN=your_serpstat_api_token_here
   ```

4. **Make the launch script executable:**
   ```bash
   chmod +x run.sh
   ```

5. **Start the server:**
   
## Tools

### `get_domains_info`
Get comprehensive domain information using Serpstat API. Returns visibility, keywords count, estimated traffic, dynamics and PPC data for multiple domains.

### `domain_regions_count`
Analyze domain keyword presence across all Google regional databases. Shows keyword count by country, regional performance comparison and international SEO insights. Start every complex domain analysis with this tool.

### `domain_keywords`
Get keywords that domain ranks for in Google search results. Includes position, traffic, difficulty analysis with comprehensive SEO insights and performance metrics.

### `get_domain_competitors`
Get top 20 domain competitors from search results with SEO metrics, traffic, visibility, and relevance score.

### `get_backlinks_summary`
Get comprehensive backlinks summary using Serpstat API. Returns referring domains, backlinks count, link types, quality metrics and recent changes for domain or subdomain.

### `api_stats`
Get current API usage statistics and credit limits. Shows remaining credits, usage percentage, and provides recommendations for optimal usage.


### Tool Parameters and Prompts:

- **domains** \(required\): Array of domains for analysis.
- **se** \(optional\): Search engine \(default: "g_us"\).
- **limit** \(optional\): Result limit \(default: 100\).

## Examples & Parameters

### Example Request via MCP:

```json
{
  "tool": "get_domains_info",
  "arguments": {
    "domains": ["example.com", "google.com"],
    "se": "g_us",
    "limit": 50
  }
}
```

### Example Response:

```json
{
  "status": "success",
  "domains_count": 2,
  "data": {
    "example.com": {
      "domain": "example.com",
      "traffic_cost": 12500,
      "keywords_count": 1500,
      "traffic_volume": 85000,
      "average_position": 15.2,
      "region": "US"
    },
    "google.com": {
      "domain": "google.com",
      "traffic_cost": 500000,
      "keywords_count": 250000,
      "traffic_volume": 10000000,
      "average_position": 1.8,
      "region": "US"
    }
  }
}
```

## Supported Search Engines \(parameter `se`\):

- `g_us` — Google USA \(default\)
- `g_uk` — Google UK
- `g_au` — Google Australia
- `g_ca` — Google Canada
- `g_de` — Google Germany
- `g_fr` — Google France
- `g_ua` — Google Ukraine
- see the full list at [Serpstat API Docs](https://api-docs.serpstat.com/docs/serpstat-public-api/ba97ni814ao9p-search-engine-short-names)



## Project Structure

```
/// TODO 
```

## Logging

Logs are written to:
- **File** `logs/serpstat-mcp-server.log` \(all levels\)

Log files are automatically rotated by day and size.

## Error Handling

The server handles the following types of errors:
- Missing or invalid API token.
- Invalid request parameters.
- Network and HTTP errors.
- Serpstat API errors.
- Internal server errors.

## Development

For development and testing:

```bash
# Compile
mvn compile

# Run tests
mvn test

# Build without tests
mvn package -DskipTests

# Clean
mvn clean
```

## Integration

**Set the environment variable with the API token:**

Run the following command in the terminal \(replace `your_serpstat_api_token_here` with your token\):

```bash
export SERPSTAT_API_TOKEN=your_serpstat_api_token_here
```

## Security

- The API token is passed via an environment variable.
- The token is not fully logged.
- All HTTP requests use HTTPS.
- Timeouts are configured to prevent hanging.

## License

MIT License
```