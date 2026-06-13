const path = require("path");

const packageRoot = path.resolve(__dirname, "../..");
const dataDir = process.env.MEO_DATA_DIR || path.join(packageRoot, "data");
const nodeRedUserDir = process.env.MEO_NODE_RED_USER_DIR || path.join(dataDir, "node-red");

module.exports = {
    uiPort: Number(process.env.MEO_NODE_RED_PORT || 1880),
    uiHost: process.env.MEO_NODE_RED_HOST || "0.0.0.0",
    userDir: nodeRedUserDir,
    flowFile: "flows.json",
    credentialSecret: process.env.MEO_NODE_RED_CREDENTIAL_SECRET || false,
    httpAdminRoot: "/",
    httpNodeRoot: "/api/node-red",
    editorTheme: {
        page: {
            title: "MEO 3"
        },
        header: {
            title: "MEO 3"
        }
    },
    functionGlobalContext: {
        meoApiUrl: process.env.MEO_API_URL || "http://127.0.0.1:7070"
    }
};
