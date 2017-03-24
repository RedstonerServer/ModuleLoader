command modules {
	list {
		help Lists all modules. Color indicates status: §aENABLED §cDISABLED;
		perm jutils.admin; 
		run list;
    }
    load [string:name...] {
        help (Re)-Loads a module. WARNING: Handle with care! This has direct affect on code being executed. This command will temporarily halt the main thread until the class loading operation was completed.;
        perm jtuils.admin;
        run load name;
        type console;
    }
}