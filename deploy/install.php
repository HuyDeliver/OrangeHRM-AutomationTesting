<?php
// Chỉ bootstrap database riêng của CD; dùng CLI installer có sẵn trong image 5.9.
// CLI này đã deprecated upstream nhưng vẫn tồn tại và đã kiểm tra source trong image pin.
$root = '/var/www/html';
foreach (['CD_DB_PASSWORD', 'CD_ADMIN_USERNAME', 'CD_ADMIN_PASSWORD'] as $key) {
    if (getenv($key) === false || getenv($key) === '') {
        throw new RuntimeException("Missing $key");
    }
}

$pdo = new PDO('mysql:host=db;port=3306', 'root', getenv('CD_DB_PASSWORD'));
$tables = (int) $pdo->query("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='orangehrm_cd'")->fetchColumn();
if (file_exists("$root/lib/confs/Conf.php")) {
    require "$root/lib/confs/Conf.php";
    $installed = new Conf();
    if ($installed->getDbHost() !== 'db' || $installed->getDbName() !== 'orangehrm_cd') {
        throw new RuntimeException('Config points outside the dedicated CD database');
    }
    $users = (int) $pdo->query('SELECT COUNT(*) FROM orangehrm_cd.ohrm_user')->fetchColumn();
    if ($users < 1) { throw new RuntimeException('CD installation has no user; stop rather than reinstall'); }
    echo "CD installation exists; database and config preserved. Login smoke will verify credentials.\n";
    exit(0);
}
if (($argv[1] ?? 'check') !== 'initialize') {
    throw new RuntimeException('CD is not initialized. Run deploy.ps1 -Initialize once before enabling automatic CD.');
}
if ($tables > 0) {
    throw new RuntimeException('Partial CD database found. Do not overwrite; inspect and recover CD once.');
}

$config = [
    'database' => [
        'hostName' => 'db',
        'hostPort' => 3306,
        'databaseName' => 'orangehrm_cd',
        'privilegedDatabaseUser' => 'root',
        'privilegedDatabasePassword' => getenv('CD_DB_PASSWORD'),
        'useSameDbUserForOrangeHRM' => 'y',
        'orangehrmDatabaseUser' => null,
        'orangehrmDatabasePassword' => null,
        'isExistingDatabase' => 'n',
        'enableDataEncryption' => 'n',
    ],
    'organization' => ['name' => 'OrangeHRM CD Test', 'country' => 'VN'],
    'admin' => [
        'adminUserName' => getenv('CD_ADMIN_USERNAME'),
        'adminPassword' => getenv('CD_ADMIN_PASSWORD'),
        'adminEmployeeFirstName' => 'CD',
        'adminEmployeeLastName' => 'Tester',
        'workEmail' => 'cd-test@example.invalid',
        'contactNumber' => null,
        'registrationConsent' => false,
    ],
    'license' => ['agree' => 'y'],
];

// JSON là cú pháp YAML hợp lệ; không ghép chuỗi password vào YAML.
$configFile = "$root/installer/cli_install_config.yaml";
file_put_contents($configFile, json_encode($config, JSON_THROW_ON_ERROR));
try {
    chdir($root);
    require "$root/installer/cli_install.php";
} finally {
    if (file_exists($configFile)) {
        unlink($configFile);
    }
}

if (!file_exists("$root/lib/confs/Conf.php")) {
    throw new RuntimeException('Installer did not create Conf.php');
}
