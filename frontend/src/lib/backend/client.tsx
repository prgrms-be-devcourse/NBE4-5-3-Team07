import createClient from "openapi-fetch";
import { paths } from "./apiV1/schema";

const client = createClient<paths>({
  baseUrl: `${process.env.NEXT_PUBLIC_API_BASE_URL}`,
  headers: {
    "Content-Type": "application/json",
  },
});

export default client;
